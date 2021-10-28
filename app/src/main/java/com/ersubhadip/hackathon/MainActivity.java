package com.ersubhadip.hackathon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import static java.util.Arrays.asList;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private EditText idInstructorBio,idInstructorName
            ,idDescription,idHomeLink,idImageUrl
            ,idNameOfCourse,OrderNumber,idVideoTitle
            ,idEbooksLink,videosSize,ebooksSize;
    private Button submitBtn,startBtn;
    public String kismeinDataBhare="null";
    public int vidLen,ebookLen;;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Please ensure that only correct data is being filled.
        firestore = FirebaseFirestore.getInstance();
        idInstructorBio=findViewById(R.id.idInstructorBio);
        idInstructorName=findViewById(R.id.idInstructorName);
        idDescription=findViewById(R.id.idDescription);
        idHomeLink=findViewById(R.id.idHomeLink);
        idImageUrl=findViewById(R.id.idImageUrl);
        idNameOfCourse=findViewById(R.id.idNameOfCourse);
        OrderNumber=findViewById(R.id.OrderNumber);
        idVideoTitle=findViewById(R.id.idVideoTitle);
        idEbooksLink=findViewById(R.id.idEbooksLink);
        ebooksSize=findViewById(R.id.ebooksSize);
        videosSize=findViewById(R.id.videosSize);
        startBtn=findViewById(R.id.start);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vidLen = Integer.parseInt(videosSize.getText().toString());
                ebookLen = Integer.parseInt(ebooksSize.getText().toString());
                submitBtn.setEnabled(true);
            }
        });
        Log.d("####",ebookLen + "  " + vidLen);
        submitBtn=findViewById(R.id.button);
        //Process:-Take all inputs and update them in DB, get course
        //id from stats collections for easy user
        //syncronization

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DialogInterface.OnClickListener dialOnClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==DialogInterface.BUTTON_POSITIVE){
                            //yes was clicked
                            Map<String,Object> map = new HashMap<>();
                            String str = idInstructorBio.getText().toString();
                            map.put("InstructorBio",str);
                            str=idInstructorName.getText().toString(); map.put("InstructorName",str);
                            str=idDescription.getText().toString();map.put("description",str);
                            str=idHomeLink.getText().toString();map.put("homeLink",str);
                            str=idImageUrl.getText().toString();map.put("imageUrl",str);
                            str=idNameOfCourse.getText().toString();map.put("name",str);
                            str=OrderNumber.getText().toString();map.put("orderNumber",Integer.parseInt(str));
                            //to do : separate given video and ebooks list and update th DB as custom list
                            str=idVideoTitle.getText().toString();
                            StringTokenizer st = new StringTokenizer(str,",");//make sure no delimiter other than comma.
                            // Trailing spaces would be ignored -> so don't worry about extra spaces in links
                            //ArrayList<String> videos = new ArrayList<>();
                            //List<String> videos = new ArrayList<>();
                            String[] videos = new String[vidLen];int it=0;
                            Log.d("####","User entered "+st.countTokens()+" video links");
                            while(st.hasMoreTokens()){
                                videos[it++] = st.nextToken().trim();
                                //videos.add(st.nextToken().trim());
                            }
                            Log.d("####","now putting videoTitle field in map");
                            if(videos.length!=0)
                                map.put("videoTitle",asList(videos));//this might give error


                            str=idImageUrl.getText().toString();
                            st = new StringTokenizer(str,",");
                            //ArrayList<String> ebooks = new ArrayList<>();
                            //List<String> ebooks = new ArrayList<>();
                            String[] ebooks = new String[ebookLen];int itt = 0;
                            Log.d("####","User entered "+st.countTokens()+" ebook links");
                            while(st.hasMoreTokens()){
                                ebooks[itt++] = st.nextToken().trim();
                                //ebooks.add(st.nextToken().trim());
                            }
                            Log.d("####","now putting ebooks field in map");
                            if(ebooks.length!=0)
                                map.put("ebookTitle",asList(ebooks));

                            //ebooks.clear();
                            //videos.clear();
                            ////Log.d("####",kismeinDataBhare);
                            //firestore.collection("courses").document(kismeinDataBhare).set(map, SetOptions.merge());
                            firestore.collection("adminStats").document("allCourses").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.exists()){
                                        String temp= documentSnapshot.getString("total");
                                        temp="op"+temp;
                                        map.put("courseId",temp);
                                        Log.d("####",temp);
                                        firestore.collection("courses").document(temp).set(map, SetOptions.merge());
                                        //kismeinDataBhare=temp; --> ye bisi dikkat de raha since classes badal gayi hain
                                        //Log.d("####",kismeinDataBhare);
                                        //To DO ~~~~~~~~~~
                                        //Update the collection stats otherwise this all won't work
                                    }
                                    else{
                                        Toast.makeText(MainActivity.this, "DB stats error. Tally it.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, "Some error occurred in collection stats"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                            Toast.makeText(MainActivity.this, "Updated the DB\nNow restart app", Toast.LENGTH_LONG).show();
                            setContentView(R.layout.activity_main);//reset everything the user entered to avoid duplication
                        }
                        else if(which==DialogInterface.BUTTON_NEGATIVE){
                            //no was clicked
                            Toast.makeText(MainActivity.this, "Maze le raha hai tu😅", Toast.LENGTH_SHORT).show();
                        }

                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Lock kiya jaye 🤔 ?").setPositiveButton("Yes",dialOnClickListener)
                        .setNegativeButton("No",dialOnClickListener).show();
            }
        });

    }
}