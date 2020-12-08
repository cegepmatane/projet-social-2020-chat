package poc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.HashSet;

public class UserListPane extends JPanel implements UserStatusListener, TopicListener {

    /** Chat client instance */
    private final ChatClient client;

    /** ClientDAO instance */
    private ClientDAO clientDAO;

    /** Shows the list of connected users */
    private JList<String> userList;
    /** List model of connected users */
    private DefaultListModel<String> userListModel;

    /** List of topics followed */
    private HashSet<String> topicsFollowed;
    /** Shows the list of topics */
    private JList<String> topicList;
    /** List model of topics */
    private DefaultListModel<String> topicListModel;

    /** Topic field */
    private JTextField topicField = new JTextField();

    /** Unfollow button */
    private JButton unfollowButton = new JButton("Unfollow");

    public UserListPane(ChatClient client) {
        this.client = client;
        clientDAO = new ClientDAO();
        topicsFollowed = clientDAO.getTopicsFollowed(client.getLogin());

        // Listeners
        this.client.addUserStatusListener(this);
        this.client.addTopicListener(this);

        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        topicListModel = new DefaultListModel<>();
        topicList = new JList<>(topicListModel);
        // User list panel
        JPanel p1 = new JPanel();
        p1.setLayout(new BorderLayout());
        p1.add(new JLabel("Connected users"), BorderLayout.NORTH);
        p1.add(new JScrollPane(userList), BorderLayout.CENTER);
        // Topic list panel
        JPanel p2 = new JPanel();
        p2.setLayout(new GridLayout(1,2));
        p2.add(new JLabel("Topics"));
        p2.add(topicField);
        JPanel p3 = new JPanel();
        p3.setLayout(new BorderLayout());
        p3.add(new JScrollPane(topicList), BorderLayout.CENTER);
        p3.add(p2, BorderLayout.NORTH);
        p3.add(unfollowButton, BorderLayout.SOUTH);

        setLayout(new GridLayout(1,2));
        add(p1);
        add(p3);

        // Display topics followed
        for (String topic : topicsFollowed) {
            topicListModel.addElement(topic);
        }

        // Topic added
        topicField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Get topic
                    String topic = topicField.getText();
                    if (!topic.equals("")) {
                        // Add topic
                        client.join(topic);
                        // Reset field
                        topicField.setText("");
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        // Click on a topic
        topicList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Check if it's a double click
                if (e.getClickCount() > 1) {
                    // Get clicked topic
                    String topic = topicList.getSelectedValue();
                    if (topic != null) {
                        // Create a message pane for that topic
                        MessagePane messagePane = new MessagePane(client, topic);
                        // Show the message pane in a separate window
                        JFrame f = new JFrame(topic);
                        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        f.setSize(500, 500);
                        // Add the message pane as the center component
                        f.getContentPane().add(messagePane, BorderLayout.CENTER);
                        f.setVisible(true);
                    }
                }
            }
        });

        // Click on a connected user
        userList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Check if it's a double click
                if (e.getClickCount() > 1) {
                    // Get clicked user's login
                    String login = userList.getSelectedValue();
                    if (login != null) {
                        // Create a message pane for that login
                        MessagePane messagePane = new MessagePane(client, login);
                        // Show the message pane in a separate window
                        JFrame f = new JFrame("Message: " + login);
                        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        f.setSize(500, 500);
                        // Add the message pane as the center component
                        f.getContentPane().add(messagePane, BorderLayout.CENTER);
                        f.setVisible(true);
                    }
                }
            }
        });

        // Unfollow button is clicked
        unfollowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get selected topic
                String topic = topicList.getSelectedValue();
                if (topic != null) {
                    try {
                        // Leave topic
                        client.leave(topic);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void online(String login) {
        userListModel.addElement(login);
    }

    @Override
    public void offline(String login) {
        userListModel.removeElement(login);
    }

    @Override
    public void onJoin(String topic) {
        topicListModel.addElement(topic);
    }

    @Override
    public void onLeave(String topic) {
        topicListModel.removeElement(topic);
    }
}
