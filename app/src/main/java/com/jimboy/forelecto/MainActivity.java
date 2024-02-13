package com.jimboy.forelecto;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private FilipinoToBolinaoTranslator translator;
    private EditText inputText;
    private Button translateButton;
    private TextView translationResult;
    private ImageButton microphoneButton, filipinoSpeakerButton, bolinaoSpeakerButton;
    private TextToSpeech filipinoTextToSpeech, bolinaoTextToSpeech;

    protected static final int RESULT_SPEECH = 1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        translator = new FilipinoToBolinaoTranslator();

        inputText = findViewById(R.id.editTextInput);
        translateButton = findViewById(R.id.translateButton);
        translationResult = findViewById(R.id.translationResult);
        microphoneButton = findViewById(R.id.microphoneButton);
        filipinoSpeakerButton = findViewById(R.id.filipinoSpeakerButton);
        bolinaoSpeakerButton = findViewById(R.id.bolinaoSpeakerButton);

        filipinoTextToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int langResult = filipinoTextToSpeech.setLanguage(new Locale("fil"));
                    if (langResult == TextToSpeech.LANG_MISSING_DATA ||
                            langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                        // Handle missing data or unsupported language
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Filipino TextToSpeech initialization failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bolinaoTextToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int langResult = bolinaoTextToSpeech.setLanguage(new Locale("fil")); // Set language to Filipino
                    if (langResult == TextToSpeech.LANG_MISSING_DATA ||
                            langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                        // Handle missing data or unsupported language
                    }
                } else {
                    // Handle initialization failure
                    Toast.makeText(MainActivity.this, "Bolinao TextToSpeech initialization failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        filipinoTextToSpeech.setOnUtteranceProgressListener(new MyUtteranceListener());

        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = inputText.getText().toString().trim().toLowerCase();
                String bolinaoTranslation = translator.translate(userInput);
                translationResult.setText(bolinaoTranslation);
            }
        });

        microphoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSpeechRecognition();
            }
        });

        filipinoSpeakerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filipinoText = inputText.getText().toString();
                speakFilipinoText(filipinoText);
            }
        });

        bolinaoSpeakerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bolinaoText = translationResult.getText().toString();
                if (!bolinaoText.isEmpty()) {
                    bolinaoTextToSpeech.speak(bolinaoText, TextToSpeech.QUEUE_FLUSH, null, null);
                } else {
                    Toast.makeText(MainActivity.this, "Nothing to speak", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void startSpeechRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fil-PH");
        try {
            startActivityForResult(intent, RESULT_SPEECH);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Your device doesn't support Speech to Text", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void speakFilipinoText(String text) {
        if (!text.isEmpty()) {
            HashMap<String, String> params = new HashMap<>();
            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "speakFilipinoText");
            filipinoTextToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, params);
        }
    }

    private class MyUtteranceListener extends UtteranceProgressListener {
        @Override
        public void onStart(String utteranceId) {
            // Handle start of speaking for Filipino
        }

        @Override
        public void onDone(String utteranceId) {
            if (utteranceId.equals("speakFilipinoText")) {
                // Do something after speaking Filipino text is done
            }
        }

        @Override
        public void onError(String utteranceId) {
            // Handle errors during speaking for Filipino
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_SPEECH:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    inputText.setText(text.get(0));
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (filipinoTextToSpeech != null) {
            filipinoTextToSpeech.stop();
            filipinoTextToSpeech.shutdown();
        }
        if (bolinaoTextToSpeech != null) {
            bolinaoTextToSpeech.stop();
            bolinaoTextToSpeech.shutdown();
        }
    }
}
