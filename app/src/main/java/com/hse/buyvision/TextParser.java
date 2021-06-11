package com.hse.buyvision;

import com.google.firebase.ml.vision.text.FirebaseVisionText;

public class TextParser {
    private static final int LENGTH_LIMIT = 150;
    private static final int NO_DIGIT = -1;
    private static int price = NO_DIGIT;

    public static String parseFirebaseVisionTextBlocks(FirebaseVisionText resultBlocks){
        StringBuilder resultText = new StringBuilder();
        for (FirebaseVisionText.TextBlock block: resultBlocks.getTextBlocks()) {
            StringBuilder blockText = new StringBuilder();
            for (FirebaseVisionText.Line line: block.getLines()) {
                for (FirebaseVisionText.Element element: line.getElements()) {
                    String elementText = element.getText();
                    price = extractNumber(elementText);
                    blockText.append(elementText).append(" ");
                }
            }
            resultText.append(blockText).append("\n");
        }
        return getPipelineResult(resultText.toString());
    }

    public static String getPipelineResult(String text){
        // PUT YOUR TRANSFORMS HERE
        text = cutOnLimit(text);
        // text = removeNumbers(text);
        text = removeTrash(text);
        return text;
    }

    public static int extractNumber(String text){
        int digit = NO_DIGIT;
        try{
            digit = Integer.parseInt(text);
            System.out.print("[Extract price] Text "+text+"is a number"+Integer.toBinaryString(digit));
        }
        catch(Exception e){
            System.out.print("[Extract price] Text "+text+"is not a number");
        }
        return digit;
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
