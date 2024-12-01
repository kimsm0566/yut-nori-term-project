package Yootgame.source.component;

import javax.swing.*;
import java.awt.*;

public class YutResultPanel extends JPanel {
    private JLabel resultImageLabel;    // 윷 던지기 결과 이미지
    private JLabel resultTextLabel;     // 윷 던지기 결과 텍스트
    private final String IMAGE_PATH = "src/main/java/Yootgame/img/yut/";  // 이미지 경로

    public YutResultPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("윷 결과"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        initializeComponents();
    }

    private void initializeComponents() {
        // 결과 이미지 레이블
        resultImageLabel = new JLabel();
        resultImageLabel.setHorizontalAlignment(JLabel.CENTER);
        resultImageLabel.setPreferredSize(new Dimension(180, 180));

        // 결과 텍스트 레이블
        resultTextLabel = new JLabel("", JLabel.CENTER);
        resultTextLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));

        // 패널에 컴포넌트 추가
        add(resultImageLabel, BorderLayout.CENTER);
        add(resultTextLabel, BorderLayout.SOUTH);

        // 초기 상태 설정
        clearResult();
    }

    // 윷 던지기 결과 업데이트
    public void updateResult(int result) {
        String imagePath = "";
        String resultText = "";

        switch(result) {
            case -1:
                imagePath = IMAGE_PATH + "backdo.png";
                resultText = "빽도";
                break;
            case 1:
                imagePath = IMAGE_PATH + "do.png";
                resultText = "도";
                break;
            case 2:
                imagePath = IMAGE_PATH + "gae.png";
                resultText = "개";
                break;
            case 3:
                imagePath = IMAGE_PATH + "geol.png";
                resultText = "걸";
                break;
            case 4:
                imagePath = IMAGE_PATH + "yut.png";
                resultText = "윷";
                break;
            case 5:
                imagePath = IMAGE_PATH + "mo.png";
                resultText = "모";
                break;
            default:
                clearResult();
                return;
        }

        // 이미지와 텍스트 업데이트
        ImageIcon icon = new ImageIcon(imagePath);
        Image img = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        resultImageLabel.setIcon(new ImageIcon(img));
        resultTextLabel.setText(resultText);
    }

    // 결과 초기화
    public void clearResult() {
        resultImageLabel.setIcon(null);
        resultTextLabel.setText("윷을 던져주세요");
    }

    // 윷 던지기 애니메이션 (선택적)
    public void playThrowAnimation() {
        // 윷 던지기 애니메이션을 위한 타이머 구현
        Timer timer = new Timer(100, e -> {
            // 애니메이션 프레임 업데이트
        });
        timer.start();
    }

    // 결과 텍스트 색상 변경 (선택적)
    public void setResultColor(Color color) {
        resultTextLabel.setForeground(color);
    }

    // 패널 크기 조정 (필요한 경우)
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(200, 250);
    }

    // 패널 크기 최대값 설정 (필요한 경우)
    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }
}