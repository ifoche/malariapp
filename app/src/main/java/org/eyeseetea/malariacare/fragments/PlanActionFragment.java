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

package org.eyeseetea.malariacare.fragments;

import static org.eyeseetea.malariacare.services.SurveyService.PREPARE_FEEDBACK_ACTION_ITEMS;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.iomodules.dhis.importer.models.EventExtended;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.data.database.utils.feedback.Feedback;
import org.eyeseetea.malariacare.layout.adapters.survey.FeedbackAdapter;
import org.eyeseetea.malariacare.layout.utils.LayoutUtils;
import org.eyeseetea.malariacare.services.SurveyService;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.views.CustomButton;
import org.eyeseetea.malariacare.views.CustomRadioButton;
import org.eyeseetea.malariacare.views.CustomTextView;

import java.util.ArrayList;
import java.util.List;

public class PlanActionFragment extends Fragment implements IModuleFragment {

    public static final String TAG = ".PlanActionFragment";

    private String moduleName;
    /**
     * Parent layout
     */
    RelativeLayout llLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        FragmentActivity faActivity = (FragmentActivity) super.getActivity();

        llLayout = (RelativeLayout) inflater.inflate(R.layout.plan_action_fragment, container, false);
        setLayoutHeaders(llLayout);
        prepareUI(moduleName);
        return llLayout; // We must return the loaded Layout
    }

    private void setLayoutHeaders(RelativeLayout llLayout) {
        Survey survey = Session.getSurveyByModule(moduleName);
        if (survey.hasMainScore()) {
            float average = survey.getMainScore();
            CustomTextView item = (CustomTextView) llLayout.findViewById(R.id.feedback_total_score);
            item.setText(String.format("%.1f%%", average));
            int colorId = LayoutUtils.trafficColor(average);
            item.setBackgroundColor(getResources().getColor(colorId));
        } else {
            CustomTextView item = (CustomTextView) llLayout.findViewById(R.id.feedback_total_score);
            item.setText(String.format("NaN"));
            float average = 0;
            int colorId = LayoutUtils.trafficColor(average);
            item.setBackgroundColor(getResources().getColor(colorId));
        }
        CustomTextView nextDate = (CustomTextView) llLayout.findViewById(R.id.plan_completion_day);
        String formattedCompletionDate="NaN";
        if(survey.getCompletionDate()!=null){
            formattedCompletionDate =  EventExtended.format(survey.getCompletionDate(),EventExtended.EUROPEAN_DATE_FORMAT);
        }
        nextDate.setText(String.format(getString(R.string.plan_action_today_date),formattedCompletionDate));

        CustomTextView completionDate = (CustomTextView) llLayout.findViewById(R.id.new_supervision_date);
        String formattedNextDate="NaN";
        if(survey.getScheduledDate()!=null){
            formattedNextDate =  EventExtended.format(survey.getScheduledDate(),EventExtended.EUROPEAN_DATE_FORMAT);
        }
        completionDate.setText(String.format(getString(R.string.plan_action_next_date),formattedNextDate));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        List<Feedback> feedbackList= new ArrayList<>();
        Session.putServiceValue(PREPARE_FEEDBACK_ACTION_ITEMS, feedbackList);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * Gets a reference to the progress view in order to stop it later
     */
    private void prepareUI(String module) {
    }

    public void setModuleName(String simpleName) {
        this.moduleName = simpleName;
    }

    @Override
    public void reloadData() {
    }

}
