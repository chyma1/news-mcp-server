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

# Country configurations
COUNTRIES = {
    'us': {'name': 'United States', 'code': 'us', 'flag': '🇺🇸'},
    'gb': {'name': 'United Kingdom', 'code': 'gb', 'flag': '🇬🇧'},
    'in': {'name': 'India', 'code': 'in', 'flag': '🇮🇳'},
    'ng': {'name': 'Nigeria', 'code': 'ng', 'flag': '🇳🇬'},
    'de': {'name': 'Germany', 'code': 'de', 'flag': '🇩🇪'},
    'fr': {'name': 'France', 'code': 'fr', 'flag': '🇫🇷'},
    'jp': {'name': 'Japan', 'code': 'jp', 'flag': '🇯🇵'},
    'cn': {'name': 'China', 'code': 'cn', 'flag': '🇨🇳'},
    'au': {'name': 'Australia', 'code': 'au', 'flag': '🇦🇺'},
    'ca': {'name': 'Canada', 'code': 'ca', 'flag': '🇨🇦'},
    'br': {'name': 'Brazil', 'code': 'br', 'flag': '🇧🇷'},
    'za': {'name': 'South Africa', 'code': 'za', 'flag': '🇿🇦'},
    # Additional countries
    'ke': {'name': 'Kenya', 'code': 'ke', 'flag': '🇰🇪'},
    'gh': {'name': 'Ghana', 'code': 'gh', 'flag': '🇬🇭'},
    'eg': {'name': 'Egypt', 'code': 'eg', 'flag': '🇪🇬'},
    'ae': {'name': 'UAE', 'code': 'ae', 'flag': '🇦🇪'},
    'sa': {'name': 'Saudi Arabia', 'code': 'sa', 'flag': '🇸🇦'},
    'pk': {'name': 'Pakistan', 'code': 'pk', 'flag': '🇵🇰'},
    'ph': {'name': 'Philippines', 'code': 'ph', 'flag': '🇵🇭'},
    'mx': {'name': 'Mexico', 'code': 'mx', 'flag': '🇲🇽'},
    'kr': {'name': 'South Korea', 'code': 'kr', 'flag': '🇰🇷'},
    'sg': {'name': 'Singapore', 'code': 'sg', 'flag': '🇸🇬'},
    'tr': {'name': 'Turkey', 'code': 'tr', 'flag': '🇹🇷'},
    'it': {'name': 'Italy', 'code': 'it', 'flag': '🇮🇹'},
    'es': {'name': 'Spain', 'code': 'es', 'flag': '🇪🇸'},
    'se': {'name': 'Sweden', 'code': 'se', 'flag': '🇸🇪'},
    'nl': {'name': 'Netherlands', 'code': 'nl', 'flag': '🇳🇱'},
    'nz': {'name': 'New Zealand', 'code': 'nz', 'flag': '🇳🇿'},
    'ie': {'name': 'Ireland', 'code': 'ie', 'flag': '🇮🇪'},
}

# Current country
current_country = 'us'

# ========== CACHE ==========
# Per-country cache - each country has its own news list
country_cache = {}  # {country_code: [articles]}
last_refresh_time = {}  # {country_code: timestamp}
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


# Country mapping for Currents API (uses full country codes)
CURRENTS_COUNTRY_MAP = {
    'us': 'US', 'gb': 'GB', 'uk': 'GB',
    'ca': 'CA', 'au': 'AU', 'in': 'IN',
    'ng': 'NG', 'za': 'ZA', 'de': 'DE',
    'fr': 'FR', 'jp': 'JP', 'cn': 'CN',
    'br': 'BR', 'ae': 'AE', 'sa': 'SA',
    'pk': 'PK', 'ph': 'PH', 'mx': 'MX',
    'kr': 'KR', 'sg': 'SG', 'tr': 'TR',
    'it': 'IT', 'es': 'ES', 'se': 'SE',
    'nl': 'NL', 'nz': 'NZ', 'ie': 'IE',
}

def fetch_from_currents(category='general', country='us'):
    """Fetch from Currents API - 600 requests/day"""
    try:
        currents_country = CURRENTS_COUNTRY_MAP.get(country, 'US')
        url = f"https://api.currentsapi.services/v1/latest?category={category}&country={currents_country}&language=en&apiKey={CURRENTS_KEY}"
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
                'category': category,
                'country': country
            } for a in articles]
    except Exception as e:
        logger.warning(f"Currents API failed: {e}")
    return []


def fetch_from_newsdata(category='general', country='us'):
    """Fetch from NewsData.io with country support"""
    try:
        # Map country codes to language codes
        country_lang_map = {
            'us': 'en', 'gb': 'en', 'in': 'en', 'ng': 'en',
            'de': 'de', 'fr': 'fr', 'jp': 'ja', 'cn': 'zh',
            'au': 'en', 'ca': 'en', 'br': 'pt', 'za': 'en'
        }
        lang = country_lang_map.get(country, 'en')
        
        url = f"https://newsdata.io/api/1/latest?apikey={NEWSDATA_KEY}&category={category}&language={lang}&country={country}"
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
                'category': a.get('category', [category])[0] if a.get('category') else category,
                'country': country
            } for a in articles if a.get('title')]
    except Exception as e:
        logger.warning(f"NewsData API failed: {e}")
    return []


# Country mapping for GNews API
GNEWS_COUNTRY_MAP = {
    'us': 'us', 'gb': 'gb', 'uk': 'gb',
    'ca': 'ca', 'au': 'au', 'in': 'in',
    'ng': 'ng', 'za': 'za', 'de': 'de',
    'fr': 'fr', 'jp': 'jp', 'cn': 'cn',
    'br': 'br', 'ae': 'ae', 'sa': 'sa',
    'pk': 'pk', 'ph': 'ph', 'mx': 'mx',
    'kr': 'kr', 'sg': 'sg', 'tr': 'tr',
    'it': 'it', 'es': 'es', 'se': 'se',
    'nl': 'nl', 'nz': 'nz', 'ie': 'ie',
}

def fetch_from_gnews(category='general', country='us'):
    """Fetch from GNews API - 100 requests/day"""
    try:
        gnews_country = GNEWS_COUNTRY_MAP.get(country, 'us')
        url = f"https://gnews.io/api/v4/topics/{category}?country={gnews_country}&lang=en&apikey={GNEWS_KEY}"
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
                'category': category,
                'country': country
            } for a in articles]
    except Exception as e:
        logger.warning(f"GNews API failed: {e}")
    return []


def fetch_from_newsapi(category='general', country='us'):
    """Fetch from NewsAPI - 100 requests/day"""
    try:
        url = f"https://newsapi.org/v2/top-headlines?country={country}&category={category}&apiKey={NEWSAPI_KEY}"
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
                'category': category,
                'country': country
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


def do_refresh(country='us'):
    """Fetch news from all APIs for a specific country"""
    global country_cache, last_refresh_time, is_refreshing, current_country
    
    if is_refreshing:
        return
    
    current_country = country
    is_refreshing = True
    try:
        logger.info(f"Fetching news from APIs for country: {country}...")
        
        all_articles = []
        
        # Try each API in order - fetch from ALL categories
        categories = ['general', 'technology', 'business', 'science', 'health', 'sports', 'entertainment']
        
        for cat in categories:  # Fetch from ALL categories
            all_articles.extend(fetch_from_newsapi(cat, country))
            all_articles.extend(fetch_from_gnews(cat, country))
            all_articles.extend(fetch_from_newsdata(cat, country))
            all_articles.extend(fetch_from_currents(cat, country))
        
        # Deduplicate
        articles = deduplicate_articles(all_articles)
        
        # Store in per-country cache
        country_cache[country] = articles
        last_refresh_time[country] = time.time()
        
        logger.info(f"Got {len(articles)} articles from APIs for {country}")
        
    except Exception as e:
        logger.error(f"Refresh failed: {e}")
        country_cache[country] = []
    finally:
        is_refreshing = False


# Start initial fetch for US
logger.info("Starting initial news fetch...")
threading.Thread(target=do_refresh, args=('us',), daemon=True).start()

# Wait for initial fetch to complete (max 30 seconds)
def wait_for_initial_fetch():
    """Wait for initial news fetch to complete"""
    max_wait = 30
    waited = 0
    while is_refreshing and waited < max_wait:
        time.sleep(1)
        waited += 1
    articles = country_cache.get('us', [])
    logger.info(f"Initial fetch complete: {len(articles)} articles")

# Run initial fetch synchronously on startup (with timeout)
wait_for_initial_fetch()


@app.route('/')
def index():
    return jsonify({
        'name': 'News API',
        'version': '1.0.0',
        'status': 'running',
        'server_ip': LOCAL_IP,
        'port': SERVER_PORT,
        'cached_items': len(country_cache.get('us', []))
    })


def get_all_categories_for_country(country):
    """Get all unique categories from cached news for a specific country"""
    categories = set()
    articles = country_cache.get(country, [])
    for article in articles:
        cat = article.get('category', '')
        if cat:
            categories.add(cat)
    return sorted(list(categories))


@app.route('/api/refresh')
def api_refresh():
    global current_country
    
    try:
        limit = int(request.args.get('limit', 50))
        force = request.args.get('force', 'false').lower() == 'true'
        country = request.args.get('country', current_country)
        
        # Get cache age for this country
        cache_time = last_refresh_time.get(country, 0)
        cache_age = time.time() - cache_time if cache_time else 9999
        
        # Get cached articles for this country
        cached_items = country_cache.get(country, [])
        
        # Trigger refresh if cache is stale, force=true, or no cache for this country
        if force or cache_age > CACHE_DURATION or len(cached_items) == 0:
            logger.info(f"Refreshing news: force={force}, cache_age={cache_age}, cached={len(cached_items)}, country={country}")
            # Clear this country's cache and refresh
            country_cache[country] = []
            threading.Thread(target=do_refresh, args=(country,), daemon=True).start()
            # Wait a bit for the refresh to complete
            time.sleep(3)
        
        # Get fresh items from cache
        items = country_cache.get(country, [])[:limit]
        
        # If still no items after refresh, return sample news for testing
        if len(items) == 0:
            items = get_sample_news()
            logger.info("Returning sample news for testing")
        
        return jsonify({
            'success': True,
            'items': items,
            'count': len(items),
            'total_count': len(country_cache.get(country, [])),
            'cache_age': int(cache_age),
            'categories': get_all_categories_for_country(country),
            'country': country
        })
    except Exception as e:
        return jsonify({'success': False, 'error': str(e)})


@app.route('/api/countries')
def get_countries():
    """Get list of available countries"""
    return jsonify({
        'success': True,
        'countries': COUNTRIES,
        'current': current_country
    })


@app.route('/api/set-country')
def set_country():
    """Set the current country"""
    global current_country
    try:
        country = request.args.get('country', 'us')
        if country in COUNTRIES:
            current_country = country
            # Clear cache and refresh
            global cached_news, last_refresh_time
            cached_news = []
            last_refresh_time = 0
            threading.Thread(target=do_refresh, args=(country,), daemon=True).start()
            time.sleep(2)
            return jsonify({
                'success': True,
                'country': current_country,
                'country_name': COUNTRIES[country]['name']
            })
        else:
            return jsonify({'success': False, 'error': 'Invalid country code'})
    except Exception as e:
        return jsonify({'success': False, 'error': str(e)})


def get_sample_news():
    """Return sample news for testing when APIs fail"""
    return [
        {
            'headline': 'Breaking: Major Technology Innovation Announced',
            'summary': 'A groundbreaking technology development has been announced that could change the way we interact with devices.',
            'source': 'TechNews',
            'source_url': 'https://www.bbc.com/news/technology',
            'published_at': '2026-03-10T10:00:00Z',
            'image_url': 'https://via.placeholder.com/800x400/4ECDC4/FFFFFF?text=Tech+News',
            'category': 'technology'
        },
        {
            'headline': 'World Markets See Significant Growth',
            'summary': 'Global markets have shown remarkable growth today as investors remain optimistic about the economic outlook.',
            'source': 'FinanceDaily',
            'source_url': 'https://www.bbc.com/news/business',
            'published_at': '2026-03-10T09:30:00Z',
            'image_url': 'https://via.placeholder.com/800x400/FF6B6B/FFFFFF?text=Finance',
            'category': 'business'
        },
        {
            'headline': 'Scientists Discover New Species in Deep Ocean',
            'summary': 'Marine biologists have discovered an entirely new species living in the depths of the Pacific Ocean.',
            'source': 'ScienceToday',
            'source_url': 'https://www.bbc.com/news/science_and_environment',
            'published_at': '2026-03-10T08:00:00Z',
            'image_url': 'https://via.placeholder.com/800x400/4ECDC4/FFFFFF?text=Science',
            'category': 'science'
        },
        {
            'headline': 'Sports: Championship Finals Set for This Weekend',
            'summary': 'The highly anticipated championship finals are set to take place this weekend with teams from around the world competing.',
            'source': 'SportsWire',
            'source_url': 'https://www.bbc.com/sport',
            'published_at': '2026-03-10T07:00:00Z',
            'image_url': 'https://via.placeholder.com/800x400/FFE66D/000000?text=Sports',
            'category': 'sports'
        },
        {
            'headline': 'Health Experts Recommend New Wellness Practices',
            'summary': 'Leading health experts are recommending new wellness practices to improve overall health and well-being.',
            'source': 'HealthNews',
            'source_url': 'https://www.bbc.com/news/health',
            'published_at': '2026-03-10T06:00:00Z',
            'image_url': 'https://via.placeholder.com/800x400/FF8A5C/FFFFFF?text=Health',
            'category': 'health'
        }
    ]


@app.route('/api/news')
def get_news():
    return api_refresh()


@app.route('/api/search')
def search_news():
    """Search news with query, category, and country"""
    try:
        query = request.args.get('q', request.args.get('query', ''))
        category = request.args.get('category', 'general')
        country = request.args.get('country', current_country)
        limit = min(int(request.args.get('limit', 50)), 100)
        
        logger.info(f"Search: q={query}, category={category}, country={country}")
        
        # For search, we need to fetch with query
        all_articles = []
        
        # Try each API with query
        try:
            # NewsAPI search
            if query:
                url = f"https://newsapi.org/v2/everything?q={query}&sortBy=publishedAt&pageSize=30&apiKey={NEWSAPI_KEY}"
                resp = requests.get(url, timeout=10)
                if resp.status_code == 200:
                    data = resp.json()
                    for a in data.get('articles', []):
                        if a.get('title') and a.get('title') != '[Removed]':
                            all_articles.append({
                                'headline': a.get('title', ''),
                                'summary': a.get('description', ''),
                                'source': a.get('source', {}).get('name', 'NewsAPI'),
                                'source_url': a.get('url', ''),
                                'published_at': a.get('publishedAt', ''),
                                'image_url': a.get('urlToImage', None),
                                'category': 'search',
                                'country': country
                            })
        except Exception as e:
            logger.warning(f"Search NewsAPI failed: {e}")
        
        # Deduplicate
        unique_articles = deduplicate_articles(all_articles)
        
        return jsonify({
            'success': True,
            'items': unique_articles[:limit],
            'count': len(unique_articles[:limit]),
            'total_count': len(unique_articles),
            'categories': ['search'],
            'country': country,
            'query': query
        })
    except Exception as e:
        return jsonify({'success': False, 'error': str(e)})


@app.route('/api/status')
def get_status():
    total_cached = sum(len(articles) for articles in country_cache.values())
    return jsonify({
        'sources': {
            'newsapi': bool(NEWSAPI_KEY),
            'gnews': bool(GNEWS_KEY),
            'currents': bool(CURRENTS_KEY),
            'newsdata': bool(NEWSDATA_KEY)
        },
        'cached': total_cached,
        'countries_cached': list(country_cache.keys())
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
