package org.imtp.client.controller;

import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;
import org.imtp.client.constant.FXMLResourceConstant;
import org.imtp.client.util.FXMLLoadUtils;
import org.imtp.client.util.ResourceUtils;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description
 * @Author ys
 * @Date 2024/10/6 21:37
 */
@Slf4j
public class VideoPlayerDialog extends Dialog<String> {

    @FXML
    private StackPane rootStackPane;

    @FXML
    private MediaView mediaView;

    @FXML
    private HBox playToolMenu;

    @FXML
    private ImageView videoImageView;

    @FXML
    private Slider slider;

    @FXML
    private HBox playAndPause;

    @FXML
    private HBox closeVideo;

    private Image playImage;

    private Image pauseImage;

    private Image replayImage;

    private static volatile VideoPlayerDialog videoPlayerDialog;

    private static final Lock lock = new ReentrantLock();

    private static final double MAX_WIDTH = 400;

    private VideoPlayerDialog() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(FXMLLoadUtils.loadUrlByFxmlPath(FXMLResourceConstant.VIDEO_PLAYER_DIALOG_FML));
        fxmlLoader.setController(this);
        try {
            DialogPane dialogPane = fxmlLoader.load();
            dialogPane.getButtonTypes().add(ButtonType.CLOSE);
            Node closeButton = dialogPane.lookupButton(ButtonType.CLOSE);
            closeButton.managedProperty().bind(closeButton.visibleProperty());
            closeButton.setVisible(false);
            setDialogPane(dialogPane);
            this.initModality(Modality.APPLICATION_MODAL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.initStyle(StageStyle.UNDECORATED);

        closeVideo.setOnMouseClicked(m -> {
            this.close();
        });
    }


    public static VideoPlayerDialog getInstance() {
        if (videoPlayerDialog == null) {
            try {
                lock.lock();
                if (videoPlayerDialog == null) {
                    videoPlayerDialog = new VideoPlayerDialog();
                }
            } finally {
                lock.unlock();
            }
        }

        return videoPlayerDialog;
    }

    @FXML
    private void initialize() {
        URL playImageUrl = ResourceUtils.classPathResource("/img/icons8-play-30.png");
        playImage = new Image(playImageUrl.toExternalForm());
        URL pauseImageUrl = ResourceUtils.classPathResource("/img/icons8-pause-30.png");
        pauseImage = new Image(pauseImageUrl.toExternalForm());
        URL replayImageUrl = ResourceUtils.classPathResource("/img/icons8-replay-24.png");
        replayImage = new Image(replayImageUrl.toExternalForm());
    }

    public void showPane(String url, double width, double height) {
        Media media = new Media(url);
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);
        if (width > MAX_WIDTH){
            mediaView.setFitWidth(MAX_WIDTH);
            mediaView.setFitHeight(MAX_WIDTH * height / width);
        }else {
            mediaView.setFitWidth(width);
            mediaView.setFitHeight(height);
        }
        mediaPlayer.setOnReady(() -> {
            log.info("MediaPlayer OnReady");
            //播放与暂停
            videoImageView.setImage(pauseImage);
            //播放状态 1 播放中 2 暂停播放 3 播放完成
            AtomicInteger playState = new AtomicInteger(1);
            mediaView.setOnMouseClicked(m -> {
                if (playState.get() == 1) {
                    videoImageView.setImage(playImage);
                    mediaPlayer.pause();
                    playState.set(2);
                } else if (playState.get() == 2) {
                    videoImageView.setImage(pauseImage);
                    mediaPlayer.play();
                    playState.set(1);
                } else {
                    videoImageView.setImage(pauseImage);
                    mediaPlayer.seek(mediaPlayer.getStartTime());
                    playState.set(1);
                }
            });
            playAndPause.setOnMouseClicked(m -> {
                if (playState.get() == 1) {
                    videoImageView.setImage(playImage);
                    mediaPlayer.pause();
                    playState.set(2);
                } else if (playState.get() == 2) {
                    videoImageView.setImage(pauseImage);
                    mediaPlayer.play();
                    playState.set(1);
                } else {
                    videoImageView.setImage(pauseImage);
                    mediaPlayer.seek(mediaPlayer.getStartTime());
                    playState.set(1);
                }
            });
            //播放完成
            mediaPlayer.setOnEndOfMedia(() -> {
                videoImageView.setImage(replayImage);
                playState.set(3);
            });
            //进度条最大时间绑定播放器
            slider.maxProperty().bind(Bindings.createDoubleBinding(
                    () -> mediaPlayer.getTotalDuration().toSeconds(),
                    mediaPlayer.totalDurationProperty()));
            //进度条绑定播放器
            mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                slider.setValue(newTime.toSeconds());
            });
            //进度条点击更新播放器
            slider.setOnMouseClicked(m -> {
                double x = m.getX();
                double w = slider.getWidth();
                double percentage = x / w;
                double newTime = percentage * mediaPlayer.getTotalDuration().toSeconds();
                mediaPlayer.seek(mediaPlayer.getTotalDuration().multiply(percentage));
                if(playState.get() == 3){
                    videoImageView.setImage(playImage);
                    mediaPlayer.pause();
                    playState.set(2);
                }
                slider.setValue(newTime);
            });
            //播放菜单渐退控制
            PauseTransition pauseTransition = new PauseTransition(Duration.seconds(2));
            pauseTransition.setOnFinished(f -> {
                playToolMenu.setVisible(false);
            });
            rootStackPane.setOnMouseEntered(m -> {
                pauseTransition.stop();
                playToolMenu.setVisible(true);
            });
            rootStackPane.setOnMouseExited(m -> {
                pauseTransition.playFromStart();
            });
            //关闭对话框停止播放并释放资源
            this.setOnCloseRequest((e) -> {
                if (!mediaPlayer.getStatus().equals(MediaPlayer.Status.UNKNOWN)) {
                    try {
                        mediaPlayer.dispose();
                    } catch (Exception exception) {
                        log.error("setOnCloseRequest exception: ", exception);
                    }
                }
            });
            mediaPlayer.play();
        });
        mediaPlayer.setOnError(() -> {
            log.error("mediaPlayer error: ", mediaPlayer.getError());
        });
        this.show();
    }


}
