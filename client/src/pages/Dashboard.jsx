import React, { useEffect, useState } from 'react';
import { dashboardService } from '../services/dashboard.service';
import './Dashboard.css';

// --- Category metadata: each stat is framed as a different astronomical event ---
const STAT_META = [
  {
    key: 'bookmarks',
    label: 'Bookmarks',
    tagline: 'Comet trail',
    accent: 'cyan',
    icon: (p) => (
      <svg viewBox="0 0 24 24" {...p}>
        <circle cx="17" cy="7" r="2.3" fill="currentColor" />
        <path d="M15 9 5 19M13 6.5 3 16.5M16.5 10 8 18.5" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" opacity="0.65" fill="none" />
      </svg>
    ),
    empty: {
      title: 'No comets bookmarked yet',
      body: 'Save an article from the feed and it will show up here for later.',
    },
  },
  {
    key: 'likes',
    label: 'Likes',
    tagline: 'Starburst',
    accent: 'gold',
    icon: (p) => (
      <svg viewBox="0 0 24 24" {...p}>
        <path
          fill="currentColor"
          d="M12 2c.6 5 1 8.5 10 10-9 1.5-9.4 5-10 10-.6-5-1-8.5-10-10 9-1.5 9.4-5 10-10Z"
        />
      </svg>
    ),
    empty: {
      title: 'No stars favorited yet',
      body: 'Articles you like will collect here as a personal highlight reel.',
    },
  },
  {
    key: 'comments',
    label: 'Comments',
    tagline: 'Transmission',
    accent: 'magenta',
    icon: (p) => (
      <svg viewBox="0 0 24 24" {...p}>
        <circle cx="12" cy="17.5" r="1.5" fill="currentColor" />
        <path d="M8.5 13.7a5 5 0 0 1 7 0" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" fill="none" />
        <path d="M5.3 10.6a9 9 0 0 1 13.4 0" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" fill="none" opacity="0.55" />
      </svg>
    ),
    empty: {
      title: 'No transmissions sent yet',
      body: 'Comments you post on articles will be logged here.',
    },
  },
];

const formatDate = (value) => {
  if (!value) return '';
  const d = new Date(value);
  if (Number.isNaN(d.getTime())) return '';
  return d.toLocaleDateString(undefined, { month: 'short', day: 'numeric', year: 'numeric' });
};

// Pulls the right fields regardless of which shape the API returns
const getEntryFields = (key, item) => {
  if (key === 'comments') {
    return {
      title: item.articleTitle || item.article?.title || item.newsTitle || 'Untitled article',
      snippet: item.content || item.text || item.comment || '',
      date: formatDate(item.createdAt || item.date),
      url: item.url || item.article?.url || null,
    };
  }
  return {
    title: item.title || item.headline || 'Untitled article',
    snippet: item.source || item.author || '',
    date: formatDate(item.publishedAt || item.createdAt || item.date),
    url: item.url || null,
  };
};

const OrbitLoader = () => (
  <div className="astro-loader" role="status" aria-live="polite">
    <div className="astro-loader__orbit">
      <span className="astro-loader__planet" />
    </div>
    <p>Scanning the archive…</p>
  </div>
);

const Dashboard = () => {
  const [dashboardData, setDashboardData] = useState({
    profile: null,
    stats: null,
    bookmarks: [],
    likes: [],
    comments: [],
  });

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [activePanel, setActivePanel] = useState(null);

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        setLoading(true);

        const [summaryRes, bookmarksRes, likesRes, commentsRes] = await Promise.all([
          dashboardService.getSummary(),
          dashboardService.getBookmarks(),
          dashboardService.getLikedArticles(),
          dashboardService.getMyComments(),
        ]);

        setDashboardData({
          profile: summaryRes.success ? summaryRes.profile : null,
          stats: summaryRes.success ? summaryRes.stats : null,
          bookmarks: bookmarksRes.success ? bookmarksRes.bookmarks : [],
          likes: likesRes.success ? likesRes.articles : [],
          comments: commentsRes.success ? commentsRes.comments : [],
        });
      } catch (err) {
        console.error('Error fetching dashboard data:', err);
        setError('Failed to load dashboard data. Please try again.');
      } finally {
        setLoading(false);
      }
    };

    fetchDashboardData();
  }, []);

  const togglePanel = (key) => setActivePanel((prev) => (prev === key ? null : key));

  if (loading) return <OrbitLoader />;

  if (error) {
    return (
      <div className="astro-dashboard astro-dashboard--center">
        <div className="astro-error">
          <p className="astro-error__title">Signal lost</p>
          <p>{error}</p>
        </div>
      </div>
    );
  }

  const { profile, stats } = dashboardData;
  const initials = (profile?.name || profile?.email || '?').trim().charAt(0).toUpperCase();

  return (
    <div className="astro-dashboard">
      <div className="astro-starfield" aria-hidden="true" />

      <header className="astro-header">
        <div className="astro-header__badge">{initials}</div>
        <div>
          <p className="astro-eyebrow">Mission Control</p>
          <h1>Welcome back, {profile?.name || 'Explorer'}</h1>
          {profile?.email && <p className="astro-header__email">{profile.email}</p>}
        </div>
      </header>

      <section className="astro-stats" role="tablist" aria-label="Activity overview">
        {STAT_META.map((meta) => {
          const count = stats?.[meta.key] ?? 0;
          const isActive = activePanel === meta.key;
          const Icon = meta.icon;
          return (
            <button
              key={meta.key}
              type="button"
              className={`astro-stat astro-stat--${meta.accent} ${isActive ? 'is-active' : ''}`}
              onClick={() => togglePanel(meta.key)}
              aria-expanded={isActive}
              aria-controls={`panel-${meta.key}`}
            >
              <span className="astro-stat__ring">
                <Icon className="astro-stat__icon" />
              </span>
              <span className="astro-stat__text">
                <span className="astro-stat__count">{count}</span>
                <span className="astro-stat__label">{meta.label}</span>
                <span className="astro-stat__tagline">{meta.tagline}</span>
              </span>
              <span className="astro-stat__chevron">⌄</span>
            </button>
          );
        })}
      </section>

      <section className="astro-panels">
        {STAT_META.map((meta) => {
          const isActive = activePanel === meta.key;
          const items = dashboardData[meta.key] || [];
          return (
            <div
              key={meta.key}
              id={`panel-${meta.key}`}
              className={`astro-panel astro-panel--${meta.accent} ${isActive ? 'is-open' : ''}`}
            >
              <div className="astro-panel__inner">
                <div className="astro-panel__pad">
                  {items.length === 0 ? (
                    <div className="astro-empty">
                      <p className="astro-empty__title">{meta.empty.title}</p>
                      <p className="astro-empty__body">{meta.empty.body}</p>
                    </div>
                  ) : (
                    <ul className="astro-list">
                      {items.map((item, idx) => {
                        const fields = getEntryFields(meta.key, item);
                        return (
                          <li key={item._id || item.id || idx} className="astro-list__item">
                            <div className="astro-list__main">
                              <p className="astro-list__title">{fields.title}</p>
                              {fields.snippet && <p className="astro-list__snippet">{fields.snippet}</p>}
                            </div>
                            <div className="astro-list__meta">
                              {fields.date && <span className="astro-list__date">{fields.date}</span>}
                              {fields.url && (
                                <a
                                  href={fields.url}
                                  target="_blank"
                                  rel="noopener noreferrer"
                                  className="astro-list__link"
                                >
                                  View source ↗
                                </a>
                              )}
                            </div>
                          </li>
                        );
                      })}
                    </ul>
                  )}
                </div>
              </div>
            </div>
          );
        })}
      </section>
    </div>
  );
};

export default Dashboard;