/*
 * Copyright 2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package performance;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import performance.DeepDTO.Level1DTO;
import performance.DeepDTO.Level2DTO;
import performance.DeepDTO.Level3DTO;
import performance.DeepDTO.Level4DTO;
import performance.DeepDTO.Level5DTO;

/**
 * Factory for creating DTOs used in {@link AbstractPerformanceDemo}.
 * 
 * @author Marko Kovacevic
 * @author Norman Lahme-Huetig
 */
final class DTOFactory {

    static StringOnlyDTO createStringOnlyDTO() {
        StringOnlyDTO obj = new StringOnlyDTO();
        obj.stringElement1 = "elementValue1";
        obj.stringElement2 = "elementValue2";
        obj.stringElement3 = "elementValue3";
        obj.stringElement4 = "elementValue4";
        obj.stringElement5 = "elementValue5";
        obj.stringElement6 = "elementValue6";
        obj.stringElement7 = "elementValue7";
        obj.stringElement8 = "elementValue8";
        obj.stringElement9 = "elementValue9";
        obj.stringElement10 = "elementValue10";
        return obj;
    }

    static MixedDTO createMixedDTO() {
        MixedDTO obj = new MixedDTO();
        obj.stringElement1 = "elementValue1";
        obj.stringElement2 = "elementValue2";
        obj.integerElement1 = new Integer(454);
        obj.integerElement2 = new Integer(12368);
        obj.booleanElement1 = new Boolean(true);
        obj.booleanElement2 = new Boolean(false);
        obj.dateElement1 = createDate("28.03.2008");
        obj.dateElement2 = createDate("11.11.2008");
        obj.bigDecimalElement1 = new BigDecimal(1258754448);
        obj.bigDecimalElement2 = new BigDecimal(1258754448);
        return obj;
    }

    static DeepDTO createDeepDTO() {
        Level5DTO leaf = new Level5DTO();
        leaf.stringElement = "StringValue";

        Level4DTO level5 = new Level4DTO();
        level5.stringElement = "StringValue";
        level5.level5DTOElement = leaf;

        Level3DTO level4 = new Level3DTO();
        level4.stringElement = "StringValue";
        level4.level4DTOElement = level5;

        Level2DTO level3 = new Level2DTO();
        level3.stringElement = "StringValue";
        level3.level3DTOElement = level4;

        Level1DTO level2 = new Level1DTO();
        level2.stringElement = "StringValue";
        level2.level2DTOElement = level3;

        DeepDTO obj = new DeepDTO();
        obj.stringElement1 = "StringValue1";
        obj.stringElement2 = "StringValue2";
        obj.stringElement3 = "StringValue3";
        obj.stringElement4 = "StringValue4";
        obj.stringElement5 = "StringValue5";
        obj.level1DTOElement = level2;

        return obj;
    }

    private DTOFactory() {

    }

    private static Date createDate(String date) {
        return createDate(date, "dd.MM.yyyy");
    }

    private static Date createDate(String date, String format) {
        try {
            SimpleDateFormat df = new SimpleDateFormat(format);
            return df.parse(date);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
