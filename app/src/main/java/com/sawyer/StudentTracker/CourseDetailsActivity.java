package com.sawyer.StudentTracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CourseDetailsActivity extends AppCompatActivity {
    static final String LOG_TAG = "CourseDetAct";
    TextView courseTitleTextView;
    TextView courseStartDate;
    TextView courseEndDate;
    TextView courseStatusTextView;
    ListView assessmentListView;
    Button courseNotesButton;
    Button courseMentorsButton;
    SimpleDateFormat formatter;
    int termId;
    int courseId;
    int assessmentId;
    FullDatabase db;
    Intent intent;
    Course selectedCourse;
    List<Assessment> assessmentList;
    //List<Assessment> assessmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);
        //--------- Instantiate Views and Setup Activity

        formatter = new SimpleDateFormat(getString(R.string.date_pattern));
        db = FullDatabase.getInstance(getApplicationContext());
        intent = getIntent();
        termId = intent.getIntExtra("termId", -1);
        System.out.println("received termId: " + termId);
        courseId = intent.getIntExtra("courseId", -1);
        System.out.println("received courseId: " + courseId);
        //assessmentList = db.assessmentDao().getAssessmentList(courseId);
        //--------- END Instantiate Views and Setup Activity
        // -------Attach Views

        courseNotesButton = findViewById(R.id.courseNotesButton);
        courseMentorsButton = findViewById(R.id.courseMentorsButton);
        courseTitleTextView = findViewById(R.id.courseTitleTextView);
        courseStatusTextView = findViewById(R.id.courseStatusTextView);
        courseStartDate = findViewById(R.id.courseStartDate);
        courseEndDate = findViewById(R.id.courseEndDate);
        assessmentListView = findViewById(R.id.assessmentListView);
        // -------End Attach Views

        updateViews();

        //--------- Assessment List View click function
        assessmentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("Assessment clicked at position: " + position);
                Intent intent = new Intent(getApplicationContext(), EditAssessmentActivity.class);
                assessmentId = assessmentList.get(position).getAssessment_id();
                intent.putExtra("termId", termId);
                intent.putExtra("courseId", courseId);
                intent.putExtra("assessmentId", assessmentId);
                startActivity(intent);
            }
        });
        //--------- End Assessment List View click function



        // -------------- FAB Add Assessments
        FloatingActionButton addAssessmentFAB = findViewById(R.id.addAssessmentFAB);
        addAssessmentFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("addAssessmentFAB clicked");
                //int dbCount = db.assessmentDao().getAssessmentList(courseId).size() + 1;
                Assessment tempAssessment = new Assessment();
                tempAssessment.setAssessment_name("Assessment Added ");
                tempAssessment.setAssessment_type("Performance");
                tempAssessment.setAssessment_due(Calendar.getInstance().getTime());
                tempAssessment.setAssessment_info("Assessment info here");
                tempAssessment.setAssessment_alert_name("Temp Assessment Name");
                tempAssessment.setAssessment_alert_date(Calendar.getInstance().getTime());
                //tempAssessment.setAssessment_set_alert(0);
                tempAssessment.setCourse_id_fk(courseId);
                try {
                    System.out.println("Inside Try - Add Assessment");
                    db.assessmentDao().insertAssessment(tempAssessment);

                } catch (Exception e) {System.out.println("Try inside addAssessmentFab failed");}

                updateAssessmentList();
            }
        });

        // -------------- End FAB Add Assessments
        // -------------- FAB Edit Course
        FloatingActionButton editCourseFAB = findViewById(R.id.editCourseFAB);
        editCourseFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("editCourseFAB clicked");
                Intent intent = new Intent(getApplicationContext(), EditCourseActivity.class);
                intent.putExtra("termId", termId);
                intent.putExtra("courseId", courseId);
                startActivity(intent);

            }
        });

        // -------------- End FAB Edit Course
        //------- Course Mentors Button
        courseMentorsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MentorsListActivity.class);
                intent.putExtra("termId", termId);
                intent.putExtra("courseId", courseId);
                startActivity(intent);
            }
        });
        //------- End Course Mentors Button
        //------- Course Notes Button
        courseNotesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CourseNotesActivity.class);
                intent.putExtra("termId", termId);
                intent.putExtra("courseId", courseId);
                startActivity(intent);
            }
        });
        //------- End Course Notes Button
    }

    private void updateAssessmentList() { //This updates the listView on this Course Details Activity
        assessmentList = new ArrayList<>();
        try {
            assessmentList = db.assessmentDao().getAssessmentList(courseId);
            System.out.println("Number of Rows in Course Query: " + assessmentList.size());
        } catch (Exception e) {System.out.println("could not pull query");}




        String[] items = new String[assessmentList.size()];
        if(!assessmentList.isEmpty()){
            for (int i = 0; i < assessmentList.size(); i++) {
                items[i] = assessmentList.get(i).getAssessment_name();
                System.out.println("Inside updateList Loop: " + i);

            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        assessmentListView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        selectedCourse = db.courseDao().getCourse(termId, courseId);
//        if (selectedCourse == null) {finish();}
        updateAssessmentList();
        updateViews();

    }

    private void updateViews(){

        //----------Update Views
        selectedCourse = db.courseDao().getCourse(termId, courseId);
        setTitle("Course Details: " + selectedCourse.getCourse_name() + ": " + percentComplete());

        if (selectedCourse != null) {
            Log.d(CourseDetailsActivity.LOG_TAG, "selectedCourse is Not null");
            courseTitleTextView.setText(selectedCourse.getCourse_name());
            courseStatusTextView.setText(selectedCourse.getCourse_status());
            courseStartDate.setText(formatter.format(selectedCourse.getCourse_start()));
            courseEndDate.setText(formatter.format(selectedCourse.getCourse_end()));

        } else {
            Log.d(CourseDetailsActivity.LOG_TAG, "selectedCourse is null");
            selectedCourse = new Course();
        }

        updateAssessmentList();
        //----------End Update Views
    }

    private String percentComplete() {
        //We need Start Date, End Date, and Today's Date
        //if (selectedCourse==null){selectedCourse = new Course();}
        Long start = selectedCourse.getCourse_start().getTime();
        Long end = selectedCourse.getCourse_end().getTime();
        Long now = Calendar.getInstance().getTime().getTime();
        double resultDouble = 0;
        if (now < start) {return "";}
        else if (now > end) {return "";}
        else {
            Log.d(LOG_TAG, "Now: " + now);
            Log.d(LOG_TAG, "Start: " + start);
            Log.d(LOG_TAG, "End: " + end);

            Long nowMinStart = now-start; //positive value
            Long endMinStart = end-start; //Positive Value Always
            Log.d(LOG_TAG, "nowMinStart: " + nowMinStart);
            Log.d(LOG_TAG, "endMinStart: " + endMinStart);
            double nowMinS = nowMinStart.doubleValue();
            double endMinS = endMinStart.doubleValue();
            resultDouble = (nowMinS/endMinS) * 100;
            Log.d(LOG_TAG, "nowMinS: " + nowMinS);
            Log.d(LOG_TAG, "endMinS: " + endMinS);
            Log.d(LOG_TAG, "resultDouble: " + resultDouble);

            return String.format("%.1f", resultDouble) + "%";

        }
    }
}
