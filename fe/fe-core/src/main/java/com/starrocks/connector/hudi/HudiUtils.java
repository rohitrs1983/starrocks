// Copyright 2021-present StarRocks, Inc. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.starrocks.connector.hudi;

import org.apache.hudi.common.table.timeline.HoodieActiveTimeline;
import org.apache.hudi.common.table.timeline.HoodieInstantTimeGenerator;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class HudiUtils {
    private static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static String formatQueryInstant(String queryInstant) throws ParseException {
        int instantLength = queryInstant.length();
        if (instantLength == 19 || instantLength == 23) { // for yyyy-MM-dd HH:mm:ss[.SSS]
            if (instantLength == 19) {
                queryInstant += ".000";
            }
            return HoodieInstantTimeGenerator.getInstantForDateString(queryInstant);
        } else if (instantLength == HoodieInstantTimeGenerator.SECS_INSTANT_ID_LENGTH
                || instantLength == HoodieInstantTimeGenerator.MILLIS_INSTANT_ID_LENGTH) { // for yyyyMMddHHmmss[SSS]
            HoodieActiveTimeline.parseDateFromInstantTime(queryInstant); // validate the format
            return queryInstant;
        } else if (instantLength == 10) { // for yyyy-MM-dd
            return HoodieActiveTimeline.formatDate(DEFAULT_DATE_FORMAT.parse(queryInstant));
        } else {
            throw new IllegalArgumentException("Unsupported query instant time format: " + queryInstant
                    + ", Supported time format are: 'yyyy-MM-dd HH:mm:ss[.SSS]' "
                    + "or 'yyyy-MM-dd' or 'yyyyMMddHHmmss[SSS]'");
        }
    }
}
