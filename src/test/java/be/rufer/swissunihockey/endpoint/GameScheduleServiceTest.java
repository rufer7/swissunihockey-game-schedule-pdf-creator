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

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class GameScheduleServiceTest {

    private static final String SAMPLE_FILE_NAME = "sample-file.txt";
    public static final String FILE_FORMAT = "UTF-8";

    private GameScheduleService gameScheduleService;

    @Before
    public void init() {
        gameScheduleService = new GameScheduleService();
    }

    @Test
    public void deleteFileDeletesFileWithGivenName() throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(SAMPLE_FILE_NAME, FILE_FORMAT);
        writer.close();
        gameScheduleService.deleteFile(SAMPLE_FILE_NAME);
        assertFalse((new File(String.format("./%s", SAMPLE_FILE_NAME)).exists()));
    }
}
