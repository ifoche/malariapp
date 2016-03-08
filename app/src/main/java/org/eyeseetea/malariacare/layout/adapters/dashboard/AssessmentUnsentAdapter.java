/*
 * Copyright (c) 2015.
 *
 * This file is part of Facility QA Tool App.
 *
 *  Facility QA Tool App is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Facility QA Tool App is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eyeseetea.malariacare.layout.adapters.dashboard;

import android.content.Context;
import android.view.View;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.layout.utils.LayoutUtils;
import org.eyeseetea.malariacare.views.CustomTextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AssessmentUnsentAdapter extends ADashboardAdapter{

    public AssessmentUnsentAdapter(List<Survey> items, Context context) {
        super(context);
        this.items = items;
        this.headerLayout = R.layout.assessment_unsent_header;
        this.recordLayout = R.layout.assessment_unsent_record;
        this.footerLayout = R.layout.assessment_unsent_footer;
    }


    @Override
    protected void decorateCustomColumns(Survey survey, View rowView) {
        ((CustomTextView) rowView.findViewById(R.id.score)).setText(getStatus(survey));
    }
}