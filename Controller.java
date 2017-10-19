package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.StageStyle;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable
{
    public BorderPane home;
    public ChoiceBox<String> systemOfEquation = new ChoiceBox<>();
    public Button generate;

    public VBox equationHome;

    private int equationNumber;
    private Double[][] equationData;
    private Double[] equationValues;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        for (Integer x = 2; x < 11; x += 1) {
            systemOfEquation.getItems().add(x.toString() + " Simultaneous Equations");
        }
        systemOfEquation.setValue("2 Simultaneous Equations");
    }

    @FXML
    private void generateForm()
    {
        equationHome.getChildren().clear();

        int selectedValue = systemOfEquation.getSelectionModel().getSelectedIndex()+2;
        equationNumber = selectedValue;

        for (int p = 0; p < selectedValue; p++){
            HBox e = new HBox();
            e.setSpacing(7);
            e.setAlignment(Pos.CENTER);
            e.getStyleClass().add("equation");
            Label equationNumber = new Label();
            equationNumber.setText("Equation " + String.valueOf(p+1) + " : ");
            if (p+1 == 10) equationNumber.setText("Equation " + String.valueOf(p+1) + ":");
            e.getChildren().add(equationNumber);
            for (int q = 0; q < selectedValue; q++) {
                TextField coefficient = new TextField();
                Label operation = new Label("");
                if (q != selectedValue - 1)
                    operation = new Label("+");
                Label variable = new Label("x"+String.valueOf(q+1));
                e.getChildren().addAll(coefficient, variable, operation);
            }
            Label equals = new Label("=");
            TextField value = new TextField();
            e.getChildren().addAll(equals, value);
            equationHome.getChildren().add(e);
        }
        Button getAnswer = new Button("Get Solution");
        getAnswer.setOnAction( evt -> {
            getData();
        });
        equationHome.getChildren().add(getAnswer);
    }

    private void getData () {
        String data ="";
        equationData = new Double[equationNumber][equationNumber];
        equationValues = new Double[equationNumber];
        int monitor = 0;
        for (Node n : equationHome.getChildren()) {
            if (monitor == equationHome.getChildren().size()-1)
                break;
            HBox hb = (HBox)n;
            for (int x = 0; x < hb.getChildren().size(); x += 1) {
                if (hb.getChildren().get(x).getClass().equals(TextField.class)) {
                    TextField tf = (TextField) hb.getChildren().get(x);
                    data += tf.getText()+",";
                }
            }
            data += ";";
            monitor++;
        }
        String[] ed = data.split(";");
        String[] de;
        for (int p = 0; p < ed.length; p++) {
            de = ed[p].split(",");
            for (int q = 0; q < de.length-1; q++) {
                equationData[p][q] = Double.parseDouble(de[q]);
            }
            equationValues[p] = Double.parseDouble(de[de.length-1]);
        }
        Gaussian gaussian = new Gaussian();
        double[] answer = gaussian.lsolve(equationData, equationValues);
        String answerCollation = "";
        Rectangle rectangle = new Rectangle(0, 0, home.getWidth(), home.getHeight());
        rectangle.setFill(Paint.valueOf("rgba(0,0,0,0.5)"));
        home.getChildren().add(rectangle);
        Alert answerBox = new Alert(Alert.AlertType.INFORMATION);
        answerBox.setHeaderText("Solution");
        answerBox.setTitle("Equation Solution");
        answerBox.initStyle(StageStyle.UNDECORATED);
        answerBox.setX(home.getScene().getWindow().getX()+500);
        answerBox.setY(home.getScene().getWindow().getY()+220);
        DialogPane dialogPane = answerBox.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("popup.css").toExternalForm());
        int count = 1;
        String str = "";
        for (Double a : answer) {
            str = String.valueOf(a);
            if (str.contains(".")) {
                if (str.substring(str.indexOf(".")).length() > 2)
                    str = str.substring(0, str.indexOf(".")+3);
            }
            answerCollation += ("x" + count + " : " + str + "\n");
            count++;
        }
        answerBox.setContentText("Below are the results from the equation : \n" + answerCollation);
        answerBox.showAndWait();
        home.getChildren().remove(rectangle);
    }
}
