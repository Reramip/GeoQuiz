package pers.jyb.geoquiz;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


public class QuizActivity extends AppCompatActivity {
    
    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX="index";
    private static final int REQUEST_CODE_CHEAT=0;
    private static final int MAX_CHEAT=2;

    private Button mTrueButton;
    private Button mFalseButton;
    private Button mCheatButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private TextView mQuestionTextView;

    private Question[] mQuestionBank=new Question[]{
            new Question(R.string.question_africa,false),
            new Question(R.string.question_australia,true),
            new Question(R.string.question_mideast,false),
            new Question(R.string.question_oceans,true)
    };
    private int mCurrentIndex=0;
    private int mScore=0;
    private int mCheated=0;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState: ");
        savedInstanceState.putInt(KEY_INDEX,mCurrentIndex);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle): called");
        setContentView(R.layout.activity_quiz);

        if(savedInstanceState!=null){
            mCurrentIndex=savedInstanceState.getInt(KEY_INDEX,0);

        }

        mQuestionTextView=(TextView)findViewById(R.id.question_text_view);
        updateQuestion();
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex=(1+mCurrentIndex)%mQuestionBank.length;
                updateQuestion();
            }
        });

        mTrueButton=(Button)findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });
        mFalseButton=(Button)findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });
        mCheatButton=(Button)findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCheated < MAX_CHEAT) {
                    boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                    Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
                    startActivityForResult(intent, REQUEST_CODE_CHEAT);
                }else{
                    showToast(R.string.cheat_too_much);
                }
            }
        });
        mNextButton=(ImageButton)findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex=(1+mCurrentIndex)%mQuestionBank.length;
                updateQuestion();
            }
        });
        mPrevButton=(ImageButton)findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex=mCurrentIndex-1;
                if(mCurrentIndex<0){
                    mCurrentIndex=mQuestionBank.length-1;
                }
                updateQuestion();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: called");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: called");
    }

    private void updateQuestion(){
        int question=mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    private void showToast(int messageId){
        Toast mShowToast=Toast.makeText(QuizActivity.this,messageId,Toast.LENGTH_SHORT);
        mShowToast.setGravity(Gravity.TOP,0,0);
        mShowToast.show();
    }

    private void gameOver(){
        if(Question.getAnsweredNumber()==mQuestionBank.length){
            String result="" + mScore+"/"+mQuestionBank.length;
            Toast.makeText(QuizActivity.this,result,Toast.LENGTH_LONG).show();
        }
    }

    private void checkAnswer(boolean userPressedTrue){
        int messageId=0;
        if (mQuestionBank[mCurrentIndex].isAnswered()) {
            messageId = R.string.answered;
        } else {
            if (mQuestionBank[mCurrentIndex].isCheated()) {
                messageId = R.string.judgment_toast;
            } else {
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                if (userPressedTrue == answerIsTrue) {
                    messageId = R.string.correct_toast;
                    mScore++;
                } else {
                    messageId = R.string.incorrect_toast;
                }
            }
            Question.mAnsweredNumber++;
            mQuestionBank[mCurrentIndex].setAnswered(true);
        }
        showToast(messageId);
        gameOver();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode!= Activity.RESULT_OK){
            return;
        }
        if(requestCode==REQUEST_CODE_CHEAT){
            if(data==null){
                return;
            }
            mQuestionBank[mCurrentIndex].setCheated(CheatActivity.isAnswerShown(data));
            mCheated++;
        }
    }
}
