package com.hse.buyvision;

import com.google.firebase.ml.vision.text.FirebaseVisionText;

public class TextParser {
    private static final int LENGTH_LIMIT = 150;

    private static String analyzedText = "";


    public static String parseFirebaseVisionTextBlocks(FirebaseVisionText resultBlocks){
        StringBuilder resultText = new StringBuilder();
        for (FirebaseVisionText.TextBlock block: resultBlocks.getTextBlocks()) {
            StringBuilder blockText = new StringBuilder();
            for (FirebaseVisionText.Line line: block.getLines()) {
                for (FirebaseVisionText.Element element: line.getElements()) {
                    String elementText = element.getText();
                    elementText = getPipelineResult(elementText);
                    blockText.append(elementText).append(" ");
                }
            }
            resultText.append(blockText).append("\n");
        }
        System.out.println("TEXT RECOGNITION RESULT");
        System.out.println(resultText);
        return resultText.toString();
    }

    private static String getPipelineResult(String text){
        analyzedText = text;
        removeNumbers();
        return analyzedText;
    }

    private static void translateOnRus(){
        //analyzedText = Translater.translate(analyzedText);
    }

    private static void removeNumbers(){

    }

    private static void removeData(){

    }

    private static void removeTrash(){

    }

    private static void cutOnLimit(){
        analyzedText = analyzedText.substring(0, LENGTH_LIMIT);
    }


}
