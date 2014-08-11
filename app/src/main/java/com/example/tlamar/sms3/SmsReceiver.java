package com.example.tlamar.sms3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by tlamar on 8/10/2014.
 */
public class SmsReceiver extends BroadcastReceiver {
    private boolean initialized = false;
    //private Map<String, Double> sentimentMap;
    private Map<String, double[]> sentimentMap;
    private int sentimentDimensions;
    private String[] sentimentLabels;
    private double minSentimentMagnitude;

    private void initialize() {
        sentimentMap = new HashMap<String, double[]>();
        // for now, just hardcode some possible values
        double[] a = {1.0, 0.0};
        double[] b = {0.8, 0.0};
        double[] c = {0.0, 0.5};
        double[] d = {0.0, 1.0};
        sentimentMap.put("excited", a);
        sentimentMap.put("happy",   b);
        sentimentMap.put("sad",     c);
        sentimentMap.put("upset",   d);
        sentimentMap.put("dead",    d);
        sentimentMap.put("died",    d);

        sentimentDimensions = 2;

        sentimentLabels = new String[sentimentDimensions];
        sentimentLabels[0] = "happy";
        sentimentLabels[1] = "sad";


        initialized = true;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String messageReceived = "";
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for (int i = 0; i < msgs.length; i++) {
                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                messageReceived += msgs[i].getMessageBody().toString();
                messageReceived += "\n";
            }
            String sentimentDescription;
            sentimentDescription = computeTextSentiment(messageReceived);
            String messageToDisplay;
            messageToDisplay = messageReceived + "\n" + sentimentDescription + "\n";
            Toast.makeText(context, messageToDisplay, Toast.LENGTH_SHORT).show();
            System.out.println(messageReceived);

            //String senderPhoneNumber = msgs[0].getOriginatingAddress();
        }
    }

    private String computeTextSentiment(String text) {
        if (!initialized) {
            initialize();
        }

        String[] words = text.split("\\s+");

        double[] totalSentiment = new double[sentimentDimensions];
        for (int i = 0; i < sentimentDimensions; i++) {
            totalSentiment[i] = 0.0;
        }

        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            double[] sentimentVector;
            sentimentVector = sentimentMap.get(word);
            if (sentimentVector == null) {
                // do nothing
            }
            else {
                // add every dimension
                for (int j = 0; j < sentimentDimensions; j++) {
                    totalSentiment[j] += sentimentVector[j];
                }
            }
        }

        //double[] averageSentiment = new double[sentimentDimensions];
        // assumes there is at least one dimension
        int maxDimension = 0;
        for (int i = 0; i < sentimentDimensions; i++) {
            if (totalSentiment[i] > totalSentiment[maxDimension]) {
                maxDimension = i;
            }
        }

        return sentimentLabels[maxDimension];

        // compare sentiment number/vector with possible states
        //if (average_sentiment > 0.0) {
        //    return "happy";
        //}
        //else {
        //    return "sad";
        //}
        //return "default";
    }
}
