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

    protected static Map<String, String> clubs;

    @Autowired
    private SwissunihockeyAPIClient swissunihockeyAPIClient;

    @Autowired
    private PDFGenerator pdfGenerator;

    @PostConstruct
    public void initClubMap() {
        clubs = swissunihockeyAPIClient.getClubsOfSeason(String.valueOf(java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)));
    }

    public String createPDFGameScheduleForTeam(String clubId, String teamId) {
        LOG.debug("Create PDF game schedule for team (clubId: {}, teamId: {})", clubId, teamId);
        Calendar teamCalendar = swissunihockeyAPIClient.getCalendarForTeam(teamId);
        return pdfGenerator.createPDFBasedCalendarForTeam(teamCalendar, clubs.get(clubId));
    }

    public void deleteUnusedFiles() {
        LOG.info("Delete all unused files in directory ./");
        File dir = new FileSystemResource("./").getFile();
        Assert.state(dir.isDirectory());

        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.getName().matches(".*-[0-9]{10,15}.pdf")) {
                if (file.delete()) {
                    LOG.info("File with name '{}' deleted", file.getName());
                }
            }
        }
    }
}
