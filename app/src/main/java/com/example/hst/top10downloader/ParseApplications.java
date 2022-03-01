package com.example.hst.top10downloader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by hst on 12/11/2017.
 */

public class ParseApplications {
    private static final String TAG = "ParseApplications";
    private ArrayList<FeedEntry> applications;

    public ParseApplications() {
        this.applications = new ArrayList<>();
    }

    public ArrayList<FeedEntry> getApplications() {
        return applications;
    }

    public boolean parse(String xmlData){
        boolean status = true;
        FeedEntry currentRecord = null;
        boolean inEntry = false;
        boolean inCollection = false;
        String textValue = "";

        try {
            //xml pulling
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlData));
            int eventType = xpp.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT){
                String tagName = xpp.getName();
                switch(eventType){
                    //start tag(including attributes), text in between, end tag
                    case XmlPullParser.START_TAG:
                        if("entry".equalsIgnoreCase(tagName)){
                            inEntry = true;
                            currentRecord = new FeedEntry();
                        }else if("category".equalsIgnoreCase(tagName)){
                            currentRecord.setGenre(xpp.getAttributeValue(null,"term"));
                        }else if("collection".equalsIgnoreCase(tagName)){
                            inCollection = true;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        textValue = xpp.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if(inEntry){
                            //"entry" in front to prevent null exception
                            if("entry".equalsIgnoreCase(tagName)){
                                applications.add(currentRecord);
                                inEntry = false;
                            }else if ("collection".equalsIgnoreCase(tagName)){
                                inCollection = false;
                            }else if ("name".equalsIgnoreCase(tagName) & !inCollection){
                                currentRecord.setName(textValue);
                            }else if ("artist".equalsIgnoreCase(tagName)){
                                currentRecord.setArtist(textValue);
                            }else if ("releaseDate".equalsIgnoreCase(tagName)){
                                currentRecord.setReleaseDate(textValue);
                            }else if ("image".equalsIgnoreCase(tagName)){
                                currentRecord.setImageURL(textValue);
                            }
                        }
                        break;

                    default:
                        //nothing else to do
                }
                eventType = xpp.next();
            }
//            for (FeedEntry app: applications){
//                Log.d(TAG, "*************");
//                Log.d(TAG, app.toString());
//            }

        }catch(Exception e){
            status = false;
            e.printStackTrace();
        }

        return status;
    }
}
