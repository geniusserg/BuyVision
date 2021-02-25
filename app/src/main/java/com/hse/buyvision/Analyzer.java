package com.hse.buyvision;


import com.google.firebase.ml.vision.text.FirebaseVisionText;

class Analyzer{
    private static Analyzer instance = null;
    private Analyzer(){
    }
    public static Analyzer getInstance(){
        if (instance == null){
            instance = new Analyzer();
        }
        return instance;
    }

    public String parseFirebaseVisionTextBlocks(FirebaseVisionText result){
        String resultText = "";
        for (FirebaseVisionText.TextBlock block: result.getTextBlocks()) {
            String blockText = "";
            for (FirebaseVisionText.Line line: block.getLines()) {
                for (FirebaseVisionText.Element element: line.getElements()) {
                    String elementText = element.getText();
                    blockText += elementText + " ";
                }
            }
            resultText += blockText + "\n";
        }
        System.out.println("TEXT RECOGNITION RESULT");
        System.out.println(resultText);
        return resultText;
    }
}
