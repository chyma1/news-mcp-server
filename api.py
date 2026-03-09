"""
Flask REST API for News Scraper
Uses free news APIs - deploy to cloud for no-server-needed operation
"""
import logging
import os
import re
import time
import threading
import requests
from flask import Flask, jsonify, request
from flask_cors import CORS

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)

logger = logging.getLogger(__name__)

# Create Flask app
app = Flask(__name__)
CORS(app)

# API Keys from environment
NEWSAPI_KEY = os.getenv('NEWSAPI_KEY', 'b8e4400f4fa749b28546a1677e0d613a')
GNEWS_KEY = os.getenv('GNEWS_KEY', 'afeb69b95f1591c3f72191a3a85e4386')
CURRENTS_KEY = os.getenv('CURRENTS_KEY', 'jIfNMLLxiNjP5I3R_mJUsKWdqkwcHUWjS3wktgm3IaUhG6NW')
NEWSDATA_KEY = os.getenv('NEWSDATA_KEY', 'pub_a99ea250981344579013da000348afe8')

# ========== CACHE ==========
cached_news = []
last_refresh_time = 0
is_refreshing = False
CACHE_DURATION = 300  # 5 minutes

# Get local IP
def get_local_ip():
    try:
        import socket
        s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        s.connect(("8.8.8.8", 80))
        ip = s.getsockname()[0]
        s.close()
        return ip
    except:
        return "0.0.0.0"

LOCAL_IP = get_local_ip()
SERVER_PORT = int(os.getenv('PORT', 16209))
SERVER_URL = f"http://{LOCAL_IP}:{SERVER_PORT}"
logger.info(f"Server running on: {SERVER_URL}")


def fetch_from_currents(category='general'):
    """Fetch from Currents API - 600 requests/day"""
    try:
        url = f"https://api.currentsapi.services/v1/latest?category={category}&apiKey={CURRENTS_KEY}"
        resp = requests.get(url, timeout=10)
        if resp.status_code == 200:
            data = resp.json()
            articles = data.get('news', [])
            return [{
                'headline': a.get('title', ''),
                'summary': a.get('description', ''),
                'source': a.get('author', a.get('source', {}).get('name', 'Currents')),
                'source_url': a.get('url', ''),
                'published_at': a.get('publishedAt', ''),
                'image_url': a.get('image', None),
                'category': category
            } for a in articles]
    except Exception as e:
        logger.warning(f"Currents API failed: {e}")
    return []


def fetch_from_newsdata(category='general'):
    """Fetch from NewsData.io - 200 requests/day"""
    try:
        url = f"https://newsdata.io/api/1/latest?apikey={NEWSDATA_KEY}&category={category}&language=en"
        resp = requests.get(url, timeout=10)
        if resp.status_code == 200:
            data = resp.json()
            articles = data.get('results', [])
            return [{
                'headline': a.get('title', ''),
                'summary': a.get('description', ''),
                'source': a.get('source_id', 'NewsData'),
                'source_url': a.get('link', ''),
                'published_at': a.get('pubDate', ''),
                'image_url': a.get('image_url', None),
                'category': a.get('category', [category])[0] if a.get('category') else category
            } for a in articles if a.get('title')]
    except Exception as e:
        logger.warning(f"NewsData API failed: {e}")
    return []


def fetch_from_gnews(category='general'):
    """Fetch from GNews API - 100 requests/day"""
    try:
        url = f"https://gnews.io/api/v4/topics/{category}?apikey={GNEWS_KEY}"
        resp = requests.get(url, timeout=10)
        if resp.status_code == 200:
            data = resp.json()
            articles = data.get('articles', [])
            return [{
                'headline': a.get('title', ''),
                'summary': a.get('description', ''),
                'source': a.get('source', {}).get('name', 'GNews'),
                'source_url': a.get('url', ''),
                'published_at': a.get('publishedAt', ''),
                'image_url': a.get('image', None),
                'category': category
            } for a in articles]
    except Exception as e:
        logger.warning(f"GNews API failed: {e}")
    return []


def fetch_from_newsapi(category='general'):
    """Fetch from NewsAPI - 100 requests/day"""
    try:
        url = f"https://newsapi.org/v2/top-headlines?country=us&category={category}&apiKey={NEWSAPI_KEY}"
        resp = requests.get(url, timeout=10)
        if resp.status_code == 200:
            data = resp.json()
            articles = data.get('articles', [])
            return [{
                'headline': a.get('title', ''),
                'summary': a.get('description', ''),
                'source': a.get('source', {}).get('name', 'NewsAPI'),
                'source_url': a.get('url', ''),
                'published_at': a.get('publishedAt', ''),
                'image_url': a.get('urlToImage', None),
                'category': category
            } for a in articles]
    except Exception as e:
        logger.warning(f"NewsAPI failed: {e}")
    return []


def deduplicate_articles(articles):
    """Fast O(n) deduplication"""
    seen = set()
    unique = []
    for article in articles:
        title = article.get('headline', '')
        if not title:
            continue
        key = re.sub(r'[^\w\s]', '', title.lower().strip())
        key = re.sub(r'\s+', ' ', key)[:60]
        if key and key not in seen:
            seen.add(key)
            unique.append(article)
    return unique


def do_refresh():
    """Fetch news from all APIs"""
    global cached_news, last_refresh_time, is_refreshing
    
    if is_refreshing:
        return
    
    is_refreshing = True
    try:
        logger.info("Fetching news from APIs...")
        
        all_articles = []
        
        # Try each API in order
        categories = ['general', 'technology', 'business', 'science', 'health', 'sports', 'entertainment']
        
        for cat in categories[:3]:  # Limit to 3 categories to save API calls
            all_articles.extend(fetch_from_newsapi(cat))
            all_articles.extend(fetch_from_gnews(cat))
            all_articles.extend(fetch_from_newsdata(cat))
            all_articles.extend(fetch_from_currents(cat))
        
        # Deduplicate
        cached_news = deduplicate_articles(all_articles)
        last_refresh_time = time.time()
        
        logger.info(f"Got {len(cached_news)} articles from APIs")
        
    except Exception as e:
        logger.error(f"Refresh failed: {e}")
        cached_news = []
    finally:
        is_refreshing = False


# Start initial fetch
logger.info("Starting initial news fetch...")
threading.Thread(target=do_refresh, daemon=True).start()


@app.route('/')
def index():
    return jsonify({
        'name': 'News API',
        'version': '1.0.0',
        'status': 'running',
        'server_ip': LOCAL_IP,
        'port': SERVER_PORT,
        'cached_items': len(cached_news)
    })


@app.route('/api/refresh')
def api_refresh():
    global cached_news
    
    try:
        limit = int(request.args.get('limit', 50))
        force = request.args.get('force', 'false').lower() == 'true'
        
        cache_age = time.time() - last_refresh_time if last_refresh_time else 9999
        
        # Trigger refresh if cache is stale
        if force or cache_age > CACHE_DURATION:
            threading.Thread(target=do_refresh, daemon=True).start()
        
        items = cached_news[:limit] if cached_news else []
        
        return jsonify({
            'success': True,
            'items': items,
            'count': len(items),
            'total_count': len(cached_news),
            'cache_age': int(cache_age)
        })
    except Exception as e:
        return jsonify({'success': False, 'error': str(e)})


@app.route('/api/news')
def get_news():
    return api_refresh()


@app.route('/api/status')
def get_status():
    return jsonify({
        'sources': {
            'newsapi': bool(NEWSAPI_KEY),
            'gnews': bool(GNEWS_KEY),
            'currents': bool(CURRENTS_KEY),
            'newsdata': bool(NEWSDATA_KEY)
        },
        'cached': len(cached_news)
    })


if __name__ == '__main__':
    print(f"""
===============================================
   NEWS SERVER
   (Uses Free News APIs)
===============================================
   Server URL: {SERVER_URL}
   
   API Keys loaded:
   - NewsAPI: {'Yes' if NEWSAPI_KEY else 'No'}
   - GNews: {'Yes' if GNEWS_KEY else 'No'}
   - Currents: {'Yes' if CURRENTS_KEY else 'No'}
   - NewsData: {'Yes' if NEWSDATA_KEY else 'No'}
===============================================
    """)
    app.run(host='0.0.0.0', port=SERVER_PORT, debug=True, use_reloader=False)
