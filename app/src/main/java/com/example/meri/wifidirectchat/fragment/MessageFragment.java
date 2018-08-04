package com.example.meri.wifidirectchat.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.meri.wifidirectchat.R;

public class MessageFragment extends Fragment {

    private TextView mTextView;
    private EditText mEditText;
    private Button mButton;

    private OnFragmentActionListener mOnFragmentActionListener;

    public MessageFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        init(view);
        setListeners();
    }

    private void init(View view){
        mTextView = view.findViewById(R.id.text_message_fragment_received_message);
        mEditText = view.findViewById(R.id.edit_message_fragment_message_text);
        mButton = view.findViewById(R.id.button_message_fragment_send_message);
    }

    private void setListeners(){
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnFragmentActionListener.onSendMessage(mEditText.getText().toString());
            }
        });
    }

    public void setMessage(String message){
        mTextView.setText(message);
    }

    public void setOnFragmentActionListener(OnFragmentActionListener onFragmentActionListener){
        mOnFragmentActionListener = onFragmentActionListener;
    }

    public interface OnFragmentActionListener{
        void onSendMessage(String message);
    }
}
