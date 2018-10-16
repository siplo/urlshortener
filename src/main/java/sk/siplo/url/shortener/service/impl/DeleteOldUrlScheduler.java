package sk.siplo.url.shortener.service.impl;

import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sk.siplo.url.shortener.service.UrlService;

/**
 * Created by siplo on 14/10/2018.
 */
@Component
public class DeleteOldUrlScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(DeleteOldUrlScheduler.class);

    @Resource
    private UrlService service;

    @Scheduled(cron = "0/10 * * * * *")
    public void removeOldUrl() {

        LOG.info("Time to clean URL");
        service.deleteOldUrls().subscribe(t -> LOG.info("Set as removed " + t));
    }
}
