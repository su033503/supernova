package com.example.administrator.audiorecorder;

/**
 * Created by Administrator on 2016-04-14.
 */
public class soundMix {
    static long upSideDownValue = 0;
    static long downSideUpValue = 0;
    static int SINT16_MIN = -32768;
    static int SINT16_MAX = 32767;

    soundMix (long[] RecordedVoiceData,long[] RealTimeData, long[] OutputData, int dataLength){

        long tempDownUpSideValue = 0;
        long tempUpSideDownValue = 0;
//calibrate maker loop
        for(int i=0;i<dataLength ; i++)
        {
            long summedValue = RecordedVoiceData[i] + RealTimeData[i];

            if(SINT16_MIN < summedValue && summedValue < SINT16_MAX)
            {
                //the value is within range -- good boy
            }
            else
            {
                //nasty calibration needed
                long tempCalibrateValue;
                tempCalibrateValue = Math.abs(summedValue) - SINT16_MIN; // here an optimization comes ;)

                if(summedValue < 0)
                {
                    //check the downside -- to calibrate
                    if(tempDownUpSideValue < tempCalibrateValue)
                        tempDownUpSideValue = tempCalibrateValue;
                }
                else
                {
                    //check the upside ---- to calibrate
                    if(tempUpSideDownValue < tempCalibrateValue)
                        tempUpSideDownValue = tempCalibrateValue;
                }
            }
        }

//here we need some function which will gradually set the value
        downSideUpValue = tempUpSideDownValue;
        upSideDownValue = tempUpSideDownValue;

//real mixer loop
        for(int i=0;i<dataLength;i++)
        {
            long summedValue = RecordedVoiceData[i] + RealTimeData[i];

            if(summedValue < 0)
            {
                OutputData[i] = summedValue + downSideUpValue;
            }
            else if(summedValue > 0)
            {
                OutputData[i] = summedValue - upSideDownValue;
            }
            else
            {
                OutputData[i] = summedValue;
            }
        }

    }
}
