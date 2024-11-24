package Yootgame.source;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class FirstPage extends JFrame {
	private BackgroundPanel panel = new BackgroundPanel();
	private JButton createRoomButton = new JButton("방 만들기");
	private ArrayList<JButton> roomButtons = new ArrayList<>();
	private JPanel roomPanel = new JPanel();
	private RoomSettingPanel roomSettingPanel;

	public FirstPage() {
		this.setTitle("Lobby");
		this.setSize(1000, 700);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());

		// Room List Panel
		roomPanel.setLayout(new BoxLayout(roomPanel, BoxLayout.Y_AXIS));
		JScrollPane scrollPane = new JScrollPane(roomPanel);

		// Right Panel for Room List and Button
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BorderLayout());
		rightPanel.add(scrollPane, BorderLayout.CENTER);

		// Create Room Button
		createRoomButton.setPreferredSize(new Dimension(150, 30)); // Fix button size
		createRoomButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openRoomSettingPanel();
			}
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(createRoomButton);
		rightPanel.add(buttonPanel, BorderLayout.SOUTH); // Add button at the bottom of the right panel

		panel.setLayout(new BorderLayout());
		panel.add(rightPanel, BorderLayout.EAST); // Place right panel on the right

		this.setContentPane(panel); // Set the main panel
		this.setVisible(true);
	}

	private void openRoomSettingPanel() {
		roomSettingPanel = new RoomSettingPanel(this);
		this.getContentPane().removeAll();
		this.add(roomSettingPanel);
		this.revalidate();
		this.repaint();
	}

	public void addRoom(String roomName, int pieceCount, int turnTime) {
		JButton roomButton = new JButton(roomName + " - " + pieceCount + "말, " + turnTime + "초");
		roomButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new PlayGame(pieceCount, turnTime);
				dispose(); // Close current window
			}
		});
		roomButtons.add(roomButton);
		roomPanel.add(roomButton);
		roomPanel.revalidate();
		roomPanel.repaint();

		// Return to Lobby
		this.getContentPane().removeAll();

		// Right Panel (Recreated to ensure layout remains)
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BorderLayout());
		rightPanel.add(new JScrollPane(roomPanel), BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(createRoomButton);
		rightPanel.add(buttonPanel, BorderLayout.SOUTH);

		panel.add(rightPanel, BorderLayout.EAST);
		this.setContentPane(panel);
		this.revalidate();
		this.repaint();
	}

	public JPanel getRoomPanel() {
		return roomPanel;
	}

	public JButton getCreateRoomButton() {
		return createRoomButton;
	}

	public JPanel getPanel() {
		return panel;
	}

	private static class BackgroundPanel extends JPanel {
		private Image backgroundImage;

		public BackgroundPanel() {
			try {
				backgroundImage = new ImageIcon("Yootgame/img/backgroundFicture.png").getImage();
			} catch (Exception e) {
				System.err.println("Background image not found: " + e.getMessage());
			}
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (backgroundImage != null) {
				g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
			}
		}
	}
}
