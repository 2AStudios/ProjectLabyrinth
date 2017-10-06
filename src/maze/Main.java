package maze;

import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Material;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;

public class Main extends Application {

    final Group root = new Group();
    final Xform axisGroup = new Xform();
    final Xform mazeGroup = new Xform();
    final Xform world = new Xform();
    final PerspectiveCamera camera = new PerspectiveCamera(true);
    final Xform cameraXform = new Xform();
    final Xform cameraXform2 = new Xform();
    final Xform cameraXform3 = new Xform();
    private static final double CAMERA_INITIAL_DISTANCE = -5;
    private static final double CAMERA_INITIAL_X_ANGLE = 0.0;
    private static final double CAMERA_INITIAL_Y_ANGLE = 90.0;
    private static final double CAMERA_INITIAL_Z_ANGLE = 0.0;
    private static final double CAMERA_NEAR_CLIP = 0.1;
    private static final double CAMERA_FAR_CLIP = 10000.0;
    private static final double AXIS_LENGTH = 250.0;

    private static String fileText = Main.readFile("res/mazeProgramData.txt");
    private static String[] indexList = fileText.split("\r\n");
    private static char[][] mazeList = new char[indexList[0].length()][indexList.length];
    private static int characterX = 0;
    private static int characterY = 0;
    private static int characterR = 0;
    private static boolean inAnimation = false;
    /*private static final double CONTROL_MULTIPLIER = 0.1;
    private static final double SHIFT_MULTIPLIER = 10.0;
    private static final double MOUSE_SPEED = 0.1;
    private static final double ROTATION_SPEED = 5.0;
    private static final double TRACK_SPEED = 0.3;

    double mousePosX;
    double mousePosY;
    double mouseOldX;
    double mouseOldY;
    double mouseDeltaX;
    double mouseDeltaY;*/

    //   private void buildScene() {
    //       root.getChildren().add(world);
    //   }
    private void buildCamera() {
        System.out.println("buildCamera()");
        root.getChildren().add(cameraXform);
        cameraXform.getChildren().add(cameraXform2);
        cameraXform2.getChildren().add(cameraXform3);
        cameraXform3.getChildren().add(camera);
        cameraXform3.setRotateZ(180.0);

        for(int i = 0;i<indexList[0].length();i++){
            for(int j = 0;j<indexList.length;j++){
                if(mazeList[i][j] == '^'){
                    characterX = j;
                    characterY = i;
                    camera.setTranslateZ(i*10);
                    camera.setTranslateX(j*10);
                }
            }
        }
        camera.setNearClip(CAMERA_NEAR_CLIP);
        camera.setFarClip(CAMERA_FAR_CLIP);
        camera.setFieldOfView(60);
        cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
        cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
        cameraXform.rx.setAngle(CAMERA_INITIAL_Z_ANGLE);
    }

    /*private void handleMouse(Scene scene, final Node root) {
        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent me) {
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseOldX = me.getSceneX();
                mouseOldY = me.getSceneY();
            }
        });
        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent me) {
                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
                mousePosX = me.getSceneX();
                mousePosY = me.getSceneY();
                mouseDeltaX = (mousePosX - mouseOldX);
                mouseDeltaY = (mousePosY - mouseOldY);

                double modifier = 1.0;

                if (me.isControlDown()) {
                    modifier = CONTROL_MULTIPLIER;
                }
                if (me.isShiftDown()) {
                    modifier = SHIFT_MULTIPLIER;
                }
                if (me.isPrimaryButtonDown()) {
                    cameraXform.ry.setAngle(cameraXform.ry.getAngle() - mouseDeltaX*MOUSE_SPEED*modifier*ROTATION_SPEED);
                    cameraXform.rx.setAngle(cameraXform.rx.getAngle() + mouseDeltaY*MOUSE_SPEED*modifier*ROTATION_SPEED);
                }
                else if (me.isSecondaryButtonDown()) {
                    double z = camera.getTranslateZ();
                    double newZ = z + mouseDeltaX*MOUSE_SPEED*modifier;
                    camera.setTranslateZ(newZ);
                }
                else if (me.isMiddleButtonDown()) {
                    cameraXform2.t.setX(cameraXform2.t.getX() + mouseDeltaX*MOUSE_SPEED*modifier*TRACK_SPEED);
                    cameraXform2.t.setY(cameraXform2.t.getY() + mouseDeltaY*MOUSE_SPEED*modifier*TRACK_SPEED);
                }
            }
        });
    }*/

    private void handleKeyboard(Scene scene, final Node root) {
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(!inAnimation)
                switch (event.getCode()) {
                    case W:
                        if(mazeList[characterY+1][characterX] == '$')
                            System.out.println("Win Maze.");
                        switch (characterR){
                            case 0: if(mazeList[characterY+1][characterX] == ' ') translateByY(10,camera); break;
                            case 90: if(mazeList[characterY][characterX+1] == ' ') translateByX(10,camera); break;
                            case 180: if(mazeList[characterY-1][characterX] == ' ') translateByY(-10,camera); break;
                            case 270: if(mazeList[characterY][characterX-1] == ' ') translateByX(-10,camera); break;
                        }
                        break;
                    case A:
                        rotateCamera(-90,camera);
                        break;
                    case S:
                        switch (characterR){
                            case 0: if(mazeList[characterY-1][characterX] == ' ') translateByY(-10,camera); break;
                            case 90: if(mazeList[characterY][characterX-1] == ' ') translateByX(-10,camera); break;
                            case 180: if(mazeList[characterY+1][characterX] == ' ') translateByY(10,camera); break;
                            case 270: if(mazeList[characterY][characterX+1] == ' ') translateByX(10,camera); break;
                        }
                        break;
                    case D:
                        rotateCamera(90,camera);

                        break;
                }
            }
        });
    }

    private void rotateCamera(double r, PerspectiveCamera camera){
        inAnimation = true;
        RotateTransition rt = new RotateTransition(Duration.millis(300), camera);
        rt.setAxis(Rotate.Y_AXIS);
        rt.setByAngle(r);
        rt.setCycleCount(1);
        rt.setOnFinished(new EventHandler<ActionEvent>() {
             public void handle(ActionEvent AE) {
                 inAnimation = false;
             }
        });
        rt.play();
        characterR+=r;
        if(characterR == 360) characterR = 0;
        if(characterR == -90) characterR = 270;
    }

    private void translateByX(double x, PerspectiveCamera camera){
        inAnimation = true;
        TranslateTransition tt = new TranslateTransition(Duration.millis(300), camera);
        tt.setByX(x);
        tt.setCycleCount(1);
        tt.setOnFinished(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent AE) {
                inAnimation = false;
            }
        });
        tt.play();
        if(x > 0)
            characterX++;
        else
            characterX--;
    }
    private void translateByY(double y, PerspectiveCamera camera){
        inAnimation = true;
        TranslateTransition tt = new TranslateTransition(Duration.millis(300), camera);
        tt.setByZ(y);
        tt.setCycleCount(1);
        tt.setOnFinished(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent AE) {
                inAnimation = false;
            }
        });
        tt.play();
        if(y > 0)
            characterY++;
        else
            characterY--;
    }

    private void buildMaze() {
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);

        PhongMaterial wallMaterial = new PhongMaterial();
        Image diffuseWallMap = new Image(new File("res/Cobblestone_1024_albedo.png").toURI().toString());
        Image normalWallMap = new Image(new File("res/Cobblestone_1024_normal.png").toURI().toString());
        Image roughnessWallMap = new Image(new File("res/Cobblestone_1024_roughness.png").toURI().toString());

        wallMaterial.setDiffuseMap(diffuseWallMap);
        wallMaterial.setBumpMap(normalWallMap);
        wallMaterial.setSpecularMap(roughnessWallMap);
        wallMaterial.setSpecularColor(Color.SANDYBROWN);
        //wallMaterial.setSpecularPower(32);

        PhongMaterial floorMaterial = new PhongMaterial();
        Image diffuseFloorMap = new Image(new File("res/Cobblestone5_1024_albedo.png").toURI().toString());
        Image normalFloorMap = new Image(new File("res/Cobblestone5_1024_normal.png").toURI().toString());

        floorMaterial.setDiffuseMap(diffuseFloorMap);
        floorMaterial.setBumpMap(normalFloorMap);
        floorMaterial.setSpecularColor(Color.WHITE);

        Xform mazeXform = new Xform();

        for(int i = 0;i<indexList[0].length();i++){
            for(int j = 0;j<indexList.length;j++){
                if(mazeList[i][j] == '*') {
                    Box mazeCube = new Box(10, 10, 10);
                    mazeCube.setMaterial(wallMaterial);
                    mazeCube.setTranslateX(i * 10);
                    mazeCube.setTranslateZ(j * 10);
                    mazeXform.getChildren().add(mazeCube);
                }else{
                    Box mazeCube = new Box(10, 2, 10);
                    mazeCube.setMaterial(floorMaterial);
                    mazeCube.setTranslateX(i * 10);
                    mazeCube.setTranslateY(-6);
                    mazeCube.setTranslateZ(j * 10);
                    mazeXform.getChildren().add(mazeCube);
                }
                if(mazeList[i][j] == '$') {
                    Sphere mazeGoal = new Sphere(1);
                    mazeGoal.setMaterial(redMaterial);
                    mazeGoal.setTranslateX(i * 10);
                    mazeGoal.setTranslateY(1);
                    mazeGoal.setTranslateZ(j * 10);
                    mazeXform.getChildren().add(mazeGoal);
                }
            }
        }

        mazeGroup.getChildren().add(mazeXform);

        world.getChildren().addAll(mazeGroup);
    }

    @Override
    public void start(Stage primaryStage) {
        System.out.println("start()");
        //Load Data
        for (int i=0; i<indexList.length;i++) {
            mazeList[i] = indexList[i].toCharArray();
        }

        root.getChildren().add(world);
        root.setDepthTest(DepthTest.ENABLE);

        // buildScene();
        buildCamera();
        //buildAxes();
        buildMaze();

        Scene scene = new Scene(root, 1024, 768, true);
        scene.setFill(Color.GREY);
        handleKeyboard(scene, world);
        //handleMouse(scene, world);

        primaryStage.setTitle("Maze3D Application");
        primaryStage.setScene(scene);
        primaryStage.show();

        scene.setCamera(camera);
    }
    public static void main(String[] args) {
        launch(args);
    }

    public static String readFile(String fileName){
        StringBuilder sb = new StringBuilder();
        String fileText = "";

        //Read textFile
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            fileText = sb.toString();
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return fileText;
    }
}
