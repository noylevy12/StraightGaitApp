import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import java.awt.BorderLayout;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import java.awt.Label;

public class deviceWindow {

	//GUI components
	private static JLabel lblConnectionStatus, lblLegImg,lblLegBackgroundImg, lblAngle;
	private static JButton btnMoveLeg_left, btnMoveLeg_right;
	private static ImageIcon imgIcon, backgroundImgIcon;


	static Socket socket, socketToAndroid;
	static ServerSocket serverSocket;
	static BufferedReader bufferedReader;
	static String ipAddress = "10.100.102.1" ;
	private JFrame frame;
	Random rand= new Random();  // this random number simulate the degree of the leg
	RotatedIcon rotatedIcon;
	int angle =0;




	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					deviceWindow window = new deviceWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		try {
			serverSocket = new ServerSocket(7800);

			while (true){
				socket=serverSocket.accept();
				bufferedReader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
				ipAddress=bufferedReader.readLine();
				if(ipAddress != null) {
					System.out.println("connected to IP address: "+ipAddress);
					lblConnectionStatus.setText("connected to IP address: "+ ipAddress);
					btnMoveLeg_left.setEnabled(true);
					btnMoveLeg_right.setEnabled(true);


				}else {
					System.out.println("there is no connection wifi");
					lblConnectionStatus.setText("There is no connection");
					btnMoveLeg_left.setEnabled(false);
					btnMoveLeg_right.setEnabled(false);

				}                    

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the application.
	 */
	public deviceWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.getContentPane().setBackground(Color.WHITE);
		frame.setBounds(100, 100, 452, 376);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);


		btnMoveLeg_left = new JButton("Move Left");
		btnMoveLeg_left.setIcon(new ImageIcon(deviceWindow.class.getResource("/com/sun/javafx/scene/web/skin/Undo_16x16_JFX.png")));
		btnMoveLeg_left.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnMoveLeg_left.setBackground(new Color(152, 251, 152));
		btnMoveLeg_left.setBounds(27, 274, 186, 30);
		btnMoveLeg_left.setEnabled(false);
		btnMoveLeg_left.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Obtain a degree number between [0 - 33].					
				angle = ThreadLocalRandom.current().nextInt(0, angle+1);
				System.out.println(angle);
				System.out.println(ipAddress);
				lblAngle.setText("Sample angle: "+ String.valueOf(angle) );
				rotatedIcon = new RotatedIcon(imgIcon, angle);
				lblLegImg.setIcon(rotatedIcon);
				System.out.println("writting to android");
				if(angle != 0) {
					if(angle != 32) {
						btnMoveLeg_right.setEnabled(true);
					}
					try {
						Socket socketToAndroid = new Socket(ipAddress, 7806);
						PrintWriter printWriter = new PrintWriter(socketToAndroid.getOutputStream());
						printWriter.write(String.valueOf(angle));
						printWriter.flush();
						printWriter.close();
						socketToAndroid.close();

					}catch(IOException e1){
						btnMoveLeg_left.setEnabled(false);
						lblConnectionStatus.setText("Connection refused");
						rotatedIcon = new RotatedIcon(imgIcon, 0);
						lblLegImg.setIcon(rotatedIcon);
						e1.printStackTrace();


					}
				}
				else {
					btnMoveLeg_left.setEnabled(false);
				}
			}
		});
		frame.getContentPane().add(btnMoveLeg_left);
		
		btnMoveLeg_right = new JButton("Move Right");
		btnMoveLeg_right.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnMoveLeg_right.setEnabled(false);
		btnMoveLeg_right.setBackground(new Color(152, 251, 152));
		btnMoveLeg_right.setBounds(221, 274, 186, 30);
		btnMoveLeg_right.setIcon(new ImageIcon(deviceWindow.class.getResource("/com/sun/javafx/scene/web/skin/Redo_16x16_JFX.png")));
		btnMoveLeg_right.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				angle = ThreadLocalRandom.current().nextInt(angle, 42);
				System.out.println(angle);
				System.out.println(ipAddress);
				lblAngle.setText("Sample angle: "+ String.valueOf(angle) );
				rotatedIcon = new RotatedIcon(imgIcon, angle);
				lblLegImg.setIcon(rotatedIcon);
				System.out.println("writting to android");
				if(angle != 42) {
					if(angle != 0) {
						btnMoveLeg_left.setEnabled(true);
					}
					
					try {
						// Obtain a degree number between [0 - 33].
						//					angle = rand.nextInt(33);

						Socket socketToAndroid = new Socket(ipAddress, 7806);
						PrintWriter printWriter = new PrintWriter(socketToAndroid.getOutputStream());
						printWriter.write(String.valueOf(angle));
						printWriter.flush();
						printWriter.close();
						socketToAndroid.close();

					}catch(IOException e1){
						btnMoveLeg_left.setEnabled(false);
						lblConnectionStatus.setText("Connection refused");
						rotatedIcon = new RotatedIcon(imgIcon, 0);
						lblLegImg.setIcon(rotatedIcon);
						e1.printStackTrace();


					}
				}else {
					btnMoveLeg_right.setEnabled(false);
				}


			}
		});
		frame.getContentPane().add(btnMoveLeg_right);


		JLabel lblHeader = new JLabel("Straight Gait Device simulator");
		lblHeader.setForeground(Color.WHITE);
		lblHeader.setBounds(0, 15, 434, 17);
		lblHeader.setBackground(Color.WHITE);
		lblHeader.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblHeader.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(lblHeader);

		lblConnectionStatus = new JLabel("connection status");
		lblConnectionStatus.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblConnectionStatus.setHorizontalAlignment(SwingConstants.CENTER);
		lblConnectionStatus.setBounds(0, 43, 432, 14);
		lblConnectionStatus.setText("There is no connection");
		frame.getContentPane().add(lblConnectionStatus);


		lblLegImg = new JLabel("");
		Image img = new ImageIcon(this.getClass().getResource("/foot.png")).getImage();
		imgIcon = new ImageIcon(this.getClass().getResource("/foot.png"));
		lblLegImg.setIcon(imgIcon);
		lblLegImg.setBounds(120, 68, 213, 184);		
		frame.getContentPane().add(lblLegImg);
		
		lblAngle = new JLabel("");
		lblAngle.setHorizontalAlignment(SwingConstants.CENTER);
		lblAngle.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblAngle.setBounds(137, 237, 169, 14);
		frame.getContentPane().add(lblAngle);
		
		lblLegBackgroundImg = new JLabel("");
		Image backgroundImg = new ImageIcon(this.getClass().getResource("/background.PNG")).getImage();
		backgroundImgIcon = new ImageIcon(this.getClass().getResource("/background.PNG"));
		lblLegBackgroundImg.setIcon(backgroundImgIcon);
		lblLegBackgroundImg.setBounds(-12, -81, 448, 418);		
		frame.getContentPane().add(lblLegBackgroundImg);





	}
	


}
