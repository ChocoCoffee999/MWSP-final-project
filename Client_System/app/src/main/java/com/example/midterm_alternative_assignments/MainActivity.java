package com.example.midterm_alternative_assignments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.EditText;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Button;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.time.Instant;
import java.time.Duration;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Header;
import retrofit2.http.Body;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import retrofit2.http.Query;

import java.util.concurrent.TimeUnit;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class MainActivity extends AppCompatActivity {
    //Layout
    private TableLayout tableLayout;
    private EditText userInputEditText;
    private int totalCols = 8;
    private int numRows = 0;
    private int numCols = 0;
    private List<String> userInput = new ArrayList<>();
    private Button addButton;
    private Button delButton;
    private ImageButton checkButton;

    private boolean checkFlag = false;
    private int buttonSize = 100;
    private String textData;
    private Bitmap imageData;
    private Disposable disposable;
    private Instant requestTime;
    private String baseUrl = "http://10.0.2.2:8800/";

    private LayoutInflater inflater;
    private FrameLayout container;
    private View overlayView;

    private Boolean responseCreate = false;

    private Instant utcNow;

    public interface ApiService {
        @GET("get/data")  // 서버의 엔드포인트 URL을 여기에 추가
        Call<JsonArray> fetchData(
                @Header("Accept") String Accept,
                @Header("Authorization") String Authorization,
                @Query("create_after") String create_after
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inflater = LayoutInflater.from(this);
        overlayView = inflater.inflate(R.layout.layout_response, null);
        container = findViewById(R.id.mainFrameLayout);

        tableLayout = findViewById(R.id.tableLayout);
        userInputEditText = findViewById(R.id.userInputEditText);
        addButton = findViewById(R.id.addButton);
        delButton = findViewById(R.id.delButton);
        checkButton = findViewById(R.id.checkButton);


    }

    public void onAddButtonClick(View view) {
        // "Add" 버튼을 눌렀을 때의 동작 구현
        String inputText = userInputEditText.getText().toString();
        userInput.add(inputText); // 사용자 입력을 변수에 저장

        userInputEditText.getText().clear(); // 입력란 지우기

        numCols++;

        if (numCols > totalCols) {
            numCols = 1;
            numRows+=2;
            TableRow tableRow = new TableRow(this);
            TableRow tableRow2 = new TableRow(this);
            for (int j = 0; j < numCols; j++) {
                ImageButton imageButton = new ImageButton(this);
                imageButton.setId(numRows*8+j);
                imageButton.setImageResource(R.drawable.green_button); // 이미지 설정
                imageButton.setLayoutParams(new TableRow.LayoutParams(30, 100));
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showOverlayView();

                        ImageButton cancelButton = findViewById(R.id.cancelButton);
                        cancelButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v2) {
                                removeOverlayView();
                            }
                        });
                    }
                });
                tableRow.addView(imageButton);
            }
            tableLayout.addView(tableRow, numRows);
            for (int j = 0; j < numCols; j++) {
                TextView textView = new TextView((this));
                textView.setId(numRows*8+8+j);
                textView.setText(userInput.get((numRows/2)*totalCols+j));
                textView.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                tableRow2.addView(textView);
            }
            tableLayout.addView(tableRow2, numRows+1);
        }
        else {
            if (numRows > 0 || numCols > 1) {
                tableLayout.removeViewAt(numRows+1);
                tableLayout.removeViewAt(numRows); // 마지막 행 삭제
            }
            TableRow tableRow = new TableRow(this);
            TableRow tableRow2 = new TableRow(this);
            for (int j = 0; j < numCols; j++) {
                ImageButton imageButton = new ImageButton(this);
                imageButton.setId(numRows*8+j);
                imageButton.setImageResource(R.drawable.green_button); // 이미지 설정
                imageButton.setLayoutParams(new TableRow.LayoutParams(buttonSize, buttonSize));
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showOverlayView();

                        ImageButton cancelButton = findViewById(R.id.cancelButton);
                        cancelButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v2) {
                                removeOverlayView();
                            }
                        });
                    }
                });
                tableRow.addView(imageButton);
            }
            if (numRows==0){
                for (int j = numCols; j < totalCols; j++) {
                    ImageView imageView = new ImageView(this);
                    imageView.setId(numRows*8+j);
                    imageView.setImageResource(R.drawable.free_button);
                    imageView.setLayoutParams(new TableRow.LayoutParams(100, 100));
                    tableRow.addView(imageView);
                }
            }
            tableLayout.addView(tableRow, numRows);
            for (int j = 0; j < numCols; j++) {
                TextView textView = new TextView((this));
                textView.setId(numRows*8+8+j);
                textView.setText(userInput.get((numRows/2)*totalCols+j));
                textView.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                tableRow2.addView(textView);
            }
            if (numRows==0) {
                for (int j = numCols; j < totalCols; j++) {
                    TextView textView = new TextView((this));
                    textView.setText("");
                    textView.setId(numRows*8+8+j);
                    textView.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tableRow2.addView(textView);
                }
            }
            tableLayout.addView(tableRow2, numRows+1);
        }
    }

    public void onDeleteButtonClick(View view) {
        // "Delete" 버튼을 눌렀을 때의 동작 구현
        if (numRows > 0 || numCols > 0) {
            numCols--;
            userInput.remove(userInput.size()-1);
            if (numCols == 0) {
                tableLayout.removeViewAt(numRows+1);
                tableLayout.removeViewAt(numRows); // 마지막 행 삭제
                if (numRows > 0) {
                    numRows-=2;
                    numCols = totalCols;
                }
                else numCols = 0;
            }
            else {
                tableLayout.removeViewAt(numRows+1);
                tableLayout.removeViewAt(numRows); // 마지막 행 삭제
                TableRow tableRow = new TableRow(this);
                TableRow tableRow2 = new TableRow(this);
                for (int j = 0; j < numCols; j++) {
                    ImageButton imageButton = new ImageButton(this);
                    imageButton.setId(numRows*8+j);
                    imageButton.setImageResource(R.drawable.green_button); // 이미지 설정
                    imageButton.setLayoutParams(new TableRow.LayoutParams(buttonSize, buttonSize));
                    imageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showOverlayView();

                            ImageButton cancelButton = findViewById(R.id.cancelButton);
                            cancelButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v2) {
                                    removeOverlayView();
                                }
                            });
                        }
                    });
                    tableRow.addView(imageButton);
                }
                if (numRows==0){
                    for (int j = numCols; j < totalCols; j++) {
                        ImageView imageView = new ImageView(this);
                        imageView.setId(numRows*8+j);
                        imageView.setImageResource(R.drawable.free_button);
                        imageView.setLayoutParams(new TableRow.LayoutParams(100, 100));
                        tableRow.addView(imageView);
                    }
                }
                tableLayout.addView(tableRow, numRows);
                for (int j = 0; j < numCols; j++) {
                    TextView textView = new TextView((this));
                    textView.setId(numRows*8+8+j);
                    textView.setText(userInput.get((numRows/2)*totalCols+j));
                    textView.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tableRow2.addView(textView);
                }
                if (numRows==0){
                    for (int j = numCols; j < totalCols; j++) {
                        TextView textView = new TextView((this));
                        textView.setId(numRows*8+8+j);
                        textView.setText("");
                        textView.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        tableRow2.addView(textView);
                    }
                }
                tableLayout.addView(tableRow2, numRows+1);
            }
        }
    }

    public void onCheckButtonClick(View view) {
        Log.i("onCheckButtonClick","진입");
        if (!checkFlag) {
            addButton.setEnabled(false);
            delButton.setEnabled(false);
            checkFlag = true;
            startRxJavaTask();
        }
        else{
            showAlertDialog();
        }


    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("주의");
        builder.setMessage("확인을 누르면 버튼 2가 활성화됩니다.");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // "확인"을 누르면 버튼 2를 활성화
                addButton.setEnabled(true);
                delButton.setEnabled(true);
                checkFlag = false;
                stopRxJavaTask();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {

           }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private void sendGetRequest() {
        Log.i("sendGetRequest","진입");
        // Retrofit 객체 생성
        Log.i("sendGetRequest","retrofit 전");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Log.i("sendGetRequest","retrofit 후");
        // Retrofit 서비스 인터페이스 정의
        ApiService apiService = retrofit.create(ApiService.class);
        utcNow = Instant.now();
        Duration twoHour = Duration.ofHours(12);
        utcNow = utcNow.minus(twoHour);
        //Log.i("utc",utcNow.toString());
        // GET 요청 보내기
        Call<JsonArray> call = apiService.fetchData("application/json", "Token 6a4d033dab3343bdeb4b6e79f08caf1ad26131d4", utcNow.toString()); // "your_time_here"를 Django 시간으로 대체
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(@NonNull Call<JsonArray> call, @NonNull Response<JsonArray> response) {
                if (response.isSuccessful()) {
                    JsonArray jsonArray = response.body();
                    for (int k = 0; k < jsonArray.size(); k++) {
                        JsonObject jsonObject = jsonArray.get(k).getAsJsonObject();
                        String text = jsonObject.get("text").getAsString();
                        String date = jsonObject.get("created_date").getAsString();
                        utcNow = Instant.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
                        LocalDateTime dateTime = LocalDateTime.parse(date, formatter);

                        // 필요한 정보 추출
                        String year = Integer.toString(dateTime.getYear());
                        String month = Integer.toString(dateTime.getMonthValue());
                        if (month.length() == 1) month = "0"+ month;
                        String day = Integer.toString(dateTime.getDayOfMonth());
                        if (day.length() == 1) day = "0"+ day;
                        String hour = Integer.toString(dateTime.getHour());
                        String minute = Integer.toString(dateTime.getMinute());
                        String second = Integer.toString(dateTime.getSecond());
                        String nano = Integer.toString(dateTime.getNano()).substring(0,6);

                        String imageURL = "media/intruder_image/" + year +"/"+ month +"/"+ day +"/"+ hour +"-"+ minute +"-"+ second +"-"+ nano + ".jpg";

                        if (numRows > 0 || numCols >0) {
                            for (int i = 1; i <= numRows+1; i += 2) {
                                if (i < numRows) {
                                    for (int j = 0; j < totalCols; j++) {
                                        TextView textView = findViewById(i*8+j);
                                        if (textView.getText() == "") continue;
                                        if (text.contains(textView.getText())) {
                                            ImageButton imageButton = findViewById((i-1)*8+j);
                                            imageButton.setImageResource(R.drawable.red_button);
                                            imageButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    showOverlayView();
                                                    TextView createDate = findViewById(R.id.createDate);
                                                    createDate.setText(date);
                                                    Button responseCheckButton = findViewById(R.id.responseCheckButton);
                                                    ImageView noticeImageView = findViewById(R.id.noticeImageView);
                                                    noticeImageView.setImageResource(R.drawable.red_button);
                                                    responseCheckButton.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View _v) {
                                                            imageButton.setImageResource(R.drawable.green_button);
                                                            noticeImageView.setImageResource(R.drawable.green_button);
                                                            responseCheckButton.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View _v) {
                                                                }
                                                            });

                                                            imageButton.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View _v) {
                                                                    showOverlayView();
                                                                    TextView createDate = findViewById(R.id.createDate);
                                                                    createDate.setText(date);
                                                                    ImageButton cancelButton = findViewById(R.id.cancelButton);
                                                                    ImageView responseImage = findViewById(R.id.responseImage);
                                                                    cancelButton.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View _v) {
                                                                            removeOverlayView();
                                                                        }
                                                                    });
                                                                    loadImage(imageURL, responseImage);
                                                                }
                                                            });
                                                        }
                                                    });

                                                    ImageButton cancelButton = findViewById(R.id.cancelButton);
                                                    ImageView responseImage = findViewById(R.id.responseImage);
                                                    cancelButton.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View _v) {
                                                            removeOverlayView();
                                                        }
                                                    });
                                                    loadImage(imageURL, responseImage);
                                                }
                                            });
                                        }
                                    }
                                } else {
                                    for (int j = 0; j< numCols; j++){
                                        TextView textView = findViewById(i*8+j);
                                        if (text.contains(textView.getText())) {
                                            ImageButton imageButton = findViewById((i-1)*8+j);
                                            imageButton.setImageResource(R.drawable.red_button);
                                            imageButton.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    showOverlayView();
                                                    TextView createDate = findViewById(R.id.createDate);
                                                    createDate.setText(date);
                                                    Button responseCheckButton = findViewById(R.id.responseCheckButton);
                                                    ImageView noticeImageView = findViewById(R.id.noticeImageView);
                                                    noticeImageView.setImageResource(R.drawable.red_button);
                                                    responseCheckButton.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View _v) {
                                                            imageButton.setImageResource(R.drawable.green_button);
                                                            noticeImageView.setImageResource(R.drawable.green_button);
                                                            responseCheckButton.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View _v) {
                                                                }
                                                            });

                                                            imageButton.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View _v) {
                                                                    showOverlayView();
                                                                    TextView createDate = findViewById(R.id.createDate);
                                                                    createDate.setText(date);
                                                                    ImageButton cancelButton = findViewById(R.id.cancelButton);
                                                                    ImageView responseImage = findViewById(R.id.responseImage);
                                                                    cancelButton.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View _v) {
                                                                            removeOverlayView();
                                                                        }
                                                                    });
                                                                    loadImage(imageURL, responseImage);
                                                                }
                                                            });
                                                        }
                                                    });

                                                    ImageButton cancelButton = findViewById(R.id.cancelButton);
                                                    ImageView responseImage = findViewById(R.id.responseImage);
                                                    cancelButton.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View _v) {
                                                            removeOverlayView();
                                                        }
                                                    });
                                                    loadImage(imageURL, responseImage);
                                                }
                                            });
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonArray> call, @Nullable Throwable t) {
                Log.e("sendGetRequest", "에러");
                Log.i("call", call.toString());
                Log.i("Throwable", t.toString());
            }
        });
    }

    private void startRxJavaTask() {
        // RxJava 태스크를 시작
        Log.i("startRxJavaTask","진입");
        disposable = Observable.interval(15, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        aLong -> {
                            sendGetRequest();
                        },
                        throwable -> {
                            Log.e("startRxJavaTask", "에러");
                        }
                );
    }

    private void stopRxJavaTask() {
        // RxJava 태스크를 중지
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    private void showOverlayView() {
        responseCreate = true;
        container.addView(overlayView);
    }

    private void removeOverlayView() {
        responseCreate = false;
        container.removeView(overlayView);
    }

    @Override
    public void onBackPressed() {
        if (responseCreate) {
            removeOverlayView();
        }
        else super.onBackPressed();
    }

    private void loadImage(String imageUrl, ImageView imageView) {
        Observable.fromCallable(() -> downloadImage(baseUrl+imageUrl, imageView))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> onImageDownloaded(result, imageView),
                        throwable -> onImageDownloadError(throwable));
    }

    private Bitmap downloadImage(String imageUrl, ImageView imageView) {
        Bitmap bitmap = null;
        try {
            // 이미지 URL로부터 Bitmap을 가져옴
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private void onImageDownloaded(Bitmap result, ImageView imageView) {
        // 이미지 다운로드 성공 시 처리
        if (result != null) {
            imageView.setImageBitmap(result);
        }
    }

    private void onImageDownloadError(Throwable throwable) {
        // 이미지 다운로드 실패 시 처리
        throwable.printStackTrace();
    }
}