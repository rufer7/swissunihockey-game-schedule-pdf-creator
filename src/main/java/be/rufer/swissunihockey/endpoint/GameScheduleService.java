/*
 * Copyright (C) 2015 Marc Rufer (m.rufer@gmx.ch)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.rufer.swissunihockey.endpoint;

import be.rufer.swissunihockey.client.SwissunihockeyAPIClient;
import be.rufer.swissunihockey.pdf.PDFGenerator;
import net.fortuna.ical4j.model.Calendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Map;

@Service
public class GameScheduleService {

    private static final Logger LOG = LoggerFactory.getLogger(GameScheduleService.class);
    private static final String PDF_FILE_PATTERN = ".*-[0-9]{10,15}.pdf";
    private static final String ROOT_DIRECTORY = "./";
    private static final long FIFTEEN_MINUTES_IN_MILLISECONDS = 1000L * 60L * 15L;

    protected static Map<String, String> clubs;

    @Autowired
    private SwissunihockeyAPIClient swissunihockeyAPIClient;

    @Autowired
    private PDFGenerator pdfGenerator;

    @PostConstruct
    public void initClubMap() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();

        int season = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        if (calendar.get(java.util.Calendar.MONTH) < 5)
        {
            season -= 1;
        }
        clubs = swissunihockeyAPIClient.getClubsOfSeason(String.valueOf(season));
    }

    public String createPDFGameScheduleForTeam(String clubId, String teamId) {
        LOG.debug("Create PDF game schedule for team (clubId: {}, teamId: {})", clubId, teamId);
        Calendar teamCalendar = swissunihockeyAPIClient.getCalendarForTeam(teamId);
        return pdfGenerator.createPDFBasedCalendarForTeam(teamCalendar, clubs.get(clubId));
    }

    public void deleteOldUnusedFiles() {
        LOG.info("Delete all files older than 15 minutes in directory ./");
        File dir = new FileSystemResource(ROOT_DIRECTORY).getFile();
        Assert.state(dir.isDirectory());

        File[] files = dir.listFiles();
        if (null != files) {
            for (File file : files) {
                if (isPdfFileAndOlderThanQuarterOfAnHour(file)) {
                    if (file.delete()) {
                        LOG.info("File with name '{}' deleted", file.getName());
                    }
                }
            }
        }
    }

    private boolean isPdfFileAndOlderThanQuarterOfAnHour(File file) {
        return file.getName().matches(PDF_FILE_PATTERN) && isOlderThanQuarterOfAnHour(file);
    }

    private boolean isOlderThanQuarterOfAnHour(File file) {
        long now = java.util.Calendar.getInstance().getTimeInMillis();
        long diff = now - file.lastModified();
        return diff > FIFTEEN_MINUTES_IN_MILLISECONDS;
    }
}
