package ch.creatis.bot;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIDataService;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;

public class MainActivity extends AppCompatActivity {

    private TextView reponse;
    private EditText input;
    private Button btn_send;

    private AIService aiService;
    private AIDataService aiDataService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        reponse = (TextView) findViewById(R.id.reponse);
        input = (EditText) findViewById(R.id.input);
        btn_send = (Button) findViewById(R.id.btn_send);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFlow();
            }
        });

        final AIConfiguration config = new AIConfiguration("9903806a5eef444abbe2c8036af0bc2a", AIConfiguration.SupportedLanguages.French, AIConfiguration.RecognitionEngine.System);
        aiDataService = new AIDataService(this, config);
        aiService = AIService.getService(this, config);
    }

    public void dialogFlow(){
        AIRequest aiRequest = new AIRequest();
        aiRequest.setQuery(input.getText().toString());
        input.getText().clear();

        final AsyncTask<AIRequest, Integer, AIResponse> task = new AsyncTask<AIRequest, Integer, AIResponse>() {
            private AIError aiError;

            @Override
            protected AIResponse doInBackground(AIRequest... params) {
                final AIRequest request = params[0];
                try {
                    final AIResponse response = aiDataService.request(request);
                    return response;
                } catch (final AIServiceException e) {
                    aiError = new AIError(e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(final AIResponse response) {
                if (response != null) {
                    Result result = response.getResult();
                    String text = result.getFulfillment().getSpeech();
                    reponse.setText(text);
                }
            }
        }.execute(aiRequest);
    }

}
