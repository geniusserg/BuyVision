package com.hse.buyvision;

import com.google.firebase.ml.vision.text.FirebaseVisionText;

public class TextParser {
    private static final int LENGTH_LIMIT = 150;


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

    public static String getPipelineResult(String text){
        // PUT YOUR TRANSFORMS HERE
        text = cutOnLimit(text);
        text = removeNumbers(text);
        text = removeTrash(text);
        return text;
    }

    public static String removeNumbers(String text){
        return text.replaceAll("[0-9]", " ");
    }

    public static String removeTrash(String text){
        return text.replace("&quot;", " ").
                replace("[", "").
                replace("]", "").
                replace(";", "");
    }

    public static String cutOnLimit(String text){
        return text.substring(0, Math.min(LENGTH_LIMIT, text.length()));
    }


}
