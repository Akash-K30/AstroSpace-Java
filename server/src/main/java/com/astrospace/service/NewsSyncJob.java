package com.astrospace.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Replacement for node-cron: sync every 30 minutes (at :00 and :30). Disable with app.sync.enabled=false. */
@Component
@ConditionalOnProperty(name = "app.sync.enabled", havingValue = "true", matchIfMissing = true)
public class NewsSyncJob {

    private static final Logger log = LoggerFactory.getLogger(NewsSyncJob.class);

    private final NewsSyncService sync;

    public NewsSyncJob(NewsSyncService sync) {
        this.sync = sync;
    }

    @Scheduled(cron = "0 */30 * * * *")
    public void run() {
        try {
            log.info("[Cron] Sync started");
            int n = sync.sync();
            log.info("[Cron] Sync completed successfully ({} articles seen)", n);
        } catch (Exception e) {
            log.error("[Cron] Sync failed", e);
        }
    }
}
