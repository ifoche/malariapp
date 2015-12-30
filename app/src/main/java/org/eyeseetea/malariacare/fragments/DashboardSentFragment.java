/*
 * Copyright (c) 2015.
 *
 * This file is part of Health Network QIS App.
 *
 *  Health Network QIS App is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Health Network QIS App is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eyeseetea.malariacare.fragments;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.FeedbackActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.SurveyActivity;
import org.eyeseetea.malariacare.database.model.OrgUnit;
import org.eyeseetea.malariacare.database.model.Program;
import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.database.utils.Session;
import org.eyeseetea.malariacare.layout.adapters.dashboard.AssessmentSentAdapter;
import org.eyeseetea.malariacare.layout.adapters.dashboard.IDashboardAdapter;
import org.eyeseetea.malariacare.layout.adapters.general.OrgUnitArrayAdapter;
import org.eyeseetea.malariacare.layout.adapters.general.ProgramArrayAdapter;
import org.eyeseetea.malariacare.layout.listeners.SwipeDismissListViewTouchListener;
import org.eyeseetea.malariacare.services.SurveyService;
import org.eyeseetea.malariacare.views.CustomTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class DashboardSentFragment extends ListFragment {


    public static final String TAG = ".CompletedFragment";
    private final static String ORG_UNIT_WITHOUT_FILTER ="ALL ASSESSMENTS";
    private final static String PROGRAM_WITHOUT_FILTER ="ALL ORG UNITS";
    private final static int WITHOUT_ORDER =0;
    private final static int FACILITY_ORDER =1;
    private final static int DATE_ORDER =2;
    private final static int SCORE_ORDER =3;
    private final static int REVERSE_ORDER =4;
    private static int LAST_ORDER =WITHOUT_ORDER;
    private SurveyReceiver surveyReceiver;
    private List<Survey> surveys;
    protected IDashboardAdapter adapter;
    private static int index = 0;
    List<Survey> oneSurveyForOrgUnit;
    Spinner filterSpinnerOrgUnit;
    Spinner filterSpinnerProgram;
    String orgUnitFilter= ORG_UNIT_WITHOUT_FILTER;
    String programFilter= PROGRAM_WITHOUT_FILTER;
    int orderBy=WITHOUT_ORDER;
    static boolean reverse=false;

    public DashboardSentFragment() {
        this.adapter = Session.getAdapterSent();
        this.surveys = new ArrayList();
        oneSurveyForOrgUnit = new ArrayList<>();
    }

    public static DashboardSentFragment newInstance(int index) {
        DashboardSentFragment f = new DashboardSentFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);

        return f;
    }


    public int getShownIndex() {
        return getArguments().getInt("index", 0);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        if (container == null) {
            return null;
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        initAdapter();
        initListView();
        initFilters(getView());
    }
    private void initFilters(View view) {
        filterSpinnerProgram = (Spinner) getActivity().findViewById(R.id.filter_program);

        List<Program> programList = Program.getAllPrograms();
        programList.add(0, new Program(PROGRAM_WITHOUT_FILTER));
        filterSpinnerProgram.setAdapter(new ProgramArrayAdapter(this.getActivity().getApplicationContext(), programList));
        filterSpinnerProgram.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Program program = (Program) parent.getItemAtPosition(position);
                boolean reload = false;
                if (program.getName().equals(PROGRAM_WITHOUT_FILTER)) {
                    if (programFilter != PROGRAM_WITHOUT_FILTER) {
                        programFilter = PROGRAM_WITHOUT_FILTER;
                        reload=true;
                    }
                } else {
                    if (programFilter != program.getUid()) {
                        programFilter = program.getUid();
                        reload=true;
                    }
                }
                if(reload)
                    reloadSentSurveys();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
        filterSpinnerOrgUnit = (Spinner) getActivity().findViewById(R.id.filter_orgunit);

        List<OrgUnit> orgUnitList = OrgUnit.getAllOrgUnit();
        orgUnitList.add(0, new OrgUnit(ORG_UNIT_WITHOUT_FILTER));
        filterSpinnerOrgUnit.setAdapter(new OrgUnitArrayAdapter(getActivity().getApplicationContext(), orgUnitList));
        filterSpinnerOrgUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                OrgUnit orgUnit = (OrgUnit) parent.getItemAtPosition(position);
                boolean reload = false;
                if (orgUnit.getName().equals(ORG_UNIT_WITHOUT_FILTER)) {
                    if (orgUnitFilter != ORG_UNIT_WITHOUT_FILTER) {
                        orgUnitFilter = ORG_UNIT_WITHOUT_FILTER;
                        reload = true;
                    }
                } else {
                    if (orgUnitFilter != orgUnit.getUid()) {
                        orgUnitFilter = orgUnit.getUid();
                        reload = true;
                    }
                }
                if (reload)
                    reloadSentSurveys();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }
    @Override
    public void onResume(){
        Log.d(TAG, "onResume");
        //Loading...
        setListShown(false);
        //Listen for data
        registerSurveysReceiver();
        super.onResume();
    }

    /**
     * Inits adapter.
     * Most of times is just an AssessmentAdapter.
     * In a version with several adapters in dashboard (like in 'mock' branch) a new one like the one in session is created.
     */
    private void initAdapter(){
        IDashboardAdapter adapterInSession = Session.getAdapterSent();
        if(adapterInSession == null){
            adapterInSession = new AssessmentSentAdapter(this.surveys, getActivity());
        }else{
            adapterInSession = adapterInSession.newInstance(this.surveys, getActivity());
        }
        this.adapter = adapterInSession;
        Session.setAdapterSent(this.adapter);
    }

    public void setScoreOrder()
    {
            orderBy=SCORE_ORDER;
    }

    public void setFacilityOrder()
    {
            orderBy=FACILITY_ORDER;
    }

    public void setDateOrder()
    {
            orderBy=DATE_ORDER;
    }
    @Override
    public void onListItemClick(ListView l, View v, int position, long id){
        Log.d(TAG, "onListItemClick");
        super.onListItemClick(l, v, position, id);

        //Discard clicks on header|footer (which is attended on newSurvey via super)
        if(!isPositionASurvey(position)){
            return;
        }
        //Put selected survey in session
        Session.setSurvey(surveys.get(position - 1));
        // Go to SurveyActivity
        if(PreferencesState.isPictureQuestion()){//Go to SurveyActivity
            ((DashboardActivity) getActivity()).go(SurveyActivity.class);
        }else {
            ((DashboardActivity) getActivity()).go(FeedbackActivity.class);
            getActivity().finish();
        }
    }

    @Override
    public void onStop(){
        Log.d(TAG, "onStop");
        unregisterSurveysReceiver();
        super.onStop();
    }

    /**
     * Checks if the given position points to a real survey instead of a footer or header of the listview.
     * @param position
     * @return true|false
     */
    private boolean isPositionASurvey(int position){
        return !isPositionFooter(position) && !isPositionHeader(position);
    }

    /**
     * Checks if the given position is the header of the listview instead of a real survey
     * @param position
     * @return true|false
     */
    private boolean isPositionHeader(int position){
        return position<=0;
    }

    /**
     * Checks if the given position is the footer of the listview instead of a real survey
     * @param position
     * @return true|false
     */
    private boolean isPositionFooter(int position){
        return position==(this.surveys.size()+1);
    }

    /**
     * Initializes the listview component, adding a listener for swiping right
     */
    private void initListView(){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View header = inflater.inflate(this.adapter.getHeaderLayout(), null, false);
        View footer = inflater.inflate(this.adapter.getFooterLayout(), null, false);
        CustomTextView title = (CustomTextView) getActivity().findViewById(R.id.titleCompleted);
        title.setText(adapter.getTitle());
        ListView listView = getListView();
        listView.addHeaderView(header);
        listView.addFooterView(footer);
        setListAdapter((BaseAdapter) adapter);

        // Create a ListView-specific touch listener. ListViews are given special treatment because
        // by default they handle touches for their list items... i.e. they're in charge of drawing
        // the pressed state (the list selector), handling list item clicks, etc.
        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        listView,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return position>0 && position<=surveys.size();
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (final int position : reverseSortedPositions) {
                                    new AlertDialog.Builder(getActivity())
                                            .setTitle(getActivity().getString(R.string.dialog_title_delete_survey))
                                            .setMessage(getActivity().getString(R.string.dialog_info_delete_survey))
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface arg0, int arg1) {
                                                    ((Survey)adapter.getItem(position-1)).delete();

                                                    Intent surveysIntent=new Intent(getActivity(), SurveyService.class);
                                                    surveysIntent.putExtra(SurveyService.SERVICE_METHOD, SurveyService.RELOAD_DASHBOARD_ACTION);
                                                    getActivity().startService(surveysIntent);
                                                    reloadSentSurveys();
                                                }
                                            })
                                            .setNegativeButton(android.R.string.no, null).create().show();
                                }

                            }
                        });
        listView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        listView.setOnScrollListener(touchListener.makeScrollListener());

        Session.listViewSent = listView;
    }


    /**
     * Register a survey receiver to load surveys into the listadapter
     */
    private void registerSurveysReceiver() {
        Log.d(TAG, "registerSurveysReceiver");

        if (surveyReceiver == null) {
            surveyReceiver = new SurveyReceiver();
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(surveyReceiver, new IntentFilter(SurveyService.ALL_SENT_OR_COMPLETED_SURVEYS_ACTION));
        }
    }


    /**
     * Unregisters the survey receiver.
     * It really important to do this, otherwise each receiver will invoke its code.
     */
    public void unregisterSurveysReceiver() {
        if (surveyReceiver != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(surveyReceiver);
            surveyReceiver = null;
        }
    }

    public void reloadSurveys(List<Survey> newListSurveys) {
        Log.d(TAG, "reloadSurveys (Thread: " + Thread.currentThread().getId() + "): " + newListSurveys.size());
        boolean hasSurveys = newListSurveys != null && newListSurveys.size() > 0;
        this.surveys.clear();
        this.surveys.addAll(newListSurveys);
        adapter.setItems(newListSurveys);
        this.adapter.notifyDataSetChanged();
        setListShown(true);
    }


    /**
     * filter the surveys for last survey in org unit, and set surveysForGraphic for the statistics
     */
    public void reloadSentSurveys() {
        List<Survey> surveys = (List<Survey>) Session.popServiceValue(SurveyService.ALL_SENT_OR_COMPLETED_SURVEYS_ACTION);
        HashMap<String, Survey> orgUnits;
        orgUnits = new HashMap<>();
        oneSurveyForOrgUnit = new ArrayList<>();

        for (Survey survey : surveys) {
            if (survey.isSent() || survey.isCompleted()) {
                if (survey.getOrgUnit() != null) {
                    if (!orgUnits.containsKey(survey.getTabGroup().getProgram().getUid()+survey.getOrgUnit().getUid())) {
                        filterSurvey(orgUnits, survey);
                    } else {
                        Survey surveyMapped = orgUnits.get(survey.getTabGroup().getProgram().getUid()+survey.getOrgUnit().getUid());
                        if (surveyMapped.getCompletionDate().before(survey.getCompletionDate())) {
                            orgUnits=filterSurvey(orgUnits, survey);
                        }
                    }
                }
            }
        }
        for (Survey survey : orgUnits.values()) {
            oneSurveyForOrgUnit.add(survey);
        }
        //Order the surveys, and reverse if is needed, taking the last order from LAST_ORDER
        if (orderBy != WITHOUT_ORDER) {
            reverse=false;
            if(orderBy==LAST_ORDER){
                reverse=true;
            }
            Collections.sort(oneSurveyForOrgUnit, new Comparator<Survey>() {
                public int compare(Survey survey1, Survey survey2) {
                    int compare;
                    switch (orderBy) {
                        case FACILITY_ORDER:
                            String surveyA = survey1.getOrgUnit().getName();
                            String surveyB = survey2.getOrgUnit().getName();
                            compare = surveyA.compareTo(surveyB);
                            break;
                        case DATE_ORDER:
                            compare = survey1.getCompletionDate().compareTo(survey2.getCompletionDate());
                            break;
                        case SCORE_ORDER:
                            compare = survey1.getMainScore().compareTo(survey2.getMainScore());
                            break;
                        default:
                            compare = survey1.getMainScore().compareTo(survey2.getMainScore());
                            break;
                    }

                    if (reverse) {
                        return (compare * -1);
                    }
                    return compare;
                }
            });
        }
        if (reverse) {
            LAST_ORDER=WITHOUT_ORDER;
        }
        else{
            LAST_ORDER=orderBy;
        }
        reloadSurveys(oneSurveyForOrgUnit);
    }
    private HashMap<String, Survey> filterSurvey(HashMap<String, Survey> orgUnits, Survey survey) {
        if(orgUnitFilter.equals(ORG_UNIT_WITHOUT_FILTER) || orgUnitFilter.equals(survey.getOrgUnit().getUid()))
            if(programFilter.equals(PROGRAM_WITHOUT_FILTER) || programFilter.equals(survey.getTabGroup().getProgram().getUid()))
              orgUnits.put(survey.getTabGroup().getProgram().getUid()+survey.getOrgUnit().getUid(), survey);
        return orgUnits;
    }

    /**
     * Inner private class that receives the result from the service
     */
    private class SurveyReceiver extends BroadcastReceiver {
        private SurveyReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive");
            //Listening only intents from this method
            if (SurveyService.ALL_SENT_OR_COMPLETED_SURVEYS_ACTION.equals(intent.getAction())) {
                reloadSentSurveys();
            }
        }

    }
}