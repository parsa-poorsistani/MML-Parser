import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import java.awt.*;

public class Components {

    private static String capitalize(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }

    private static Color getColor(String colorString){
        String color = colorString.toLowerCase();
        switch (color){
            case "red":
                return Color.RED;
            case "gray":
                return Color.GRAY;
            case "green":
                return Color.GREEN;
            case "blue":
                return Color.blue;
            case "yellow":
                return Color.YELLOW;
            default:
                return null;
        }
    }

    public static void parseElement(Element element, Container parent) {
        JComponent component;
        switch (element.getNodeName()) {
            case "JPanel":
                component = makeJPanel(element);
                break;
            case "JButton":
                component = makeJButton(element);
                break;
            case "JLabel":
                component = makeJLabel(element);
                break;
            case "JTextField":
                component = makeJTextField(element);
                break;
            case "JCheckBox":
                component = makeJCheckbox(element);
                break;
            default:
                throw new IllegalStateException("Unexpected tag: " + element.getNodeName());
        }

        LayoutManager parentLayout = parent.getLayout();
        if (parentLayout instanceof BorderLayout) {
            addToBorderLayout(element, component, parent);
        } else if (parentLayout instanceof FlowLayout) {
            parent.add(component);
        } else if (parentLayout instanceof GridLayout) {
            parent.add(component);
        }

        if (element.getNodeName().equals("JPanel")) {
            attachLayout(element, component);
        }

        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE)
                parseElement((Element) child, component);
        }
    }

    public static void attachBorderLayout(Container container) {
        container.setLayout(new BorderLayout());
    }

    public static void attachGridLayout(Container container, Element element) {
        GridLayout gridLayout = new GridLayout();
        if (element.hasAttribute("cols")) {
            gridLayout.setColumns(Integer.parseInt(element.getAttribute("cols")));
        } else {
            throw new IllegalStateException("required attr: cols");
        }
        if (element.hasAttribute("rows")) {
            gridLayout.setRows(Integer.parseInt(element.getAttribute("rows")));
        } else {
            throw new IllegalStateException("required attr: rows");
        }
        container.setLayout(gridLayout);
    }

    public static void attachFlowLayout(Container container, Element element) {
        FlowLayout flowLayout = new FlowLayout();
        int alignment;
        if (element.hasAttribute("alignment")) {
            switch (element.getAttribute("alignment")) {
                case "left":
                    alignment = FlowLayout.LEFT;
                    break;
                case "right":
                    alignment = FlowLayout.RIGHT;
                    break;
                case "center":
                    alignment = FlowLayout.CENTER;
                    break;
                default:
                    throw new IllegalStateException("invalid argument for attr alignment");
            }
            flowLayout.setAlignment(alignment);
            container.setLayout(flowLayout);
        } else {
            throw new IllegalStateException("required attr: cols");
        }
    }

    public static void addToBorderLayout(Element element, JComponent component, Container parent) {
        if (element.hasAttribute("layout_gravity"))
            parent.add(component, capitalize(element.getAttribute("layout_gravity")));
        else
            parent.add(component);
    }

    public static JFrame makeJFrame(Element root) {
        JFrame jFrame = new JFrame();
        if (root.hasAttribute("title"))
            jFrame.setTitle(root.getAttribute("title"));
        if (root.hasAttribute("width"))
            jFrame.setSize(Integer.parseInt(root.getAttribute("width")), jFrame.getHeight());
        else
            throw new IllegalStateException("missing JFrame attr: width");
        if (root.hasAttribute("height"))
            jFrame.setSize(jFrame.getWidth(), Integer.parseInt(root.getAttribute("height")));
        else
            throw new IllegalStateException("missing JFrame attr: height");
        return jFrame;
    }

    public static void attachLayout(Element element, Container container) {
        if (element.hasAttribute("layout")) {
            switch (element.getAttribute("layout")) {
                case "BorderLayout":
                    attachBorderLayout(container);
                    break;
                case "GridLayout":
                    attachGridLayout(container, element);
                    break;
                case "FlowLayout":
                    attachFlowLayout(container, element);
                    break;
            }
        }
    }

    public static JPanel makeJPanel(Element element) {
        JPanel jPanel = new JPanel();
        if (element.hasAttribute("background"))
            jPanel.setBackground(getColor(element.getAttribute("background")));
        return jPanel;
    }

    public static JLabel makeJLabel(Element element) {
        JLabel jLabel = new JLabel();
        if (element.hasAttribute("text"))
            jLabel.setText(element.getAttribute("text"));
        if (element.hasAttribute("tool_tip_text"))
            jLabel.setToolTipText(element.getAttribute("tool_tip_text"));
        if (element.hasAttribute("foreground"))
            jLabel.setForeground(getColor(element.getAttribute("foreground")));
        return jLabel;
    }

    public static JTextField makeJTextField(Element element) {
        JTextField textField = new JTextField();
        if (element.hasAttribute("tool_tip_text"))
            textField.setToolTipText(element.getAttribute("tool_tip_text"));
        if (element.hasAttribute("foreground"))
            textField.setForeground(getColor(element.getAttribute("foreground")));
        return textField;
    }

    public static JButton makeJButton(Element element) {
        JButton jButton = new JButton();
        if (element.hasAttribute("text"))
            jButton.setText(element.getAttribute("text"));
        if (element.hasAttribute("foreground"))
            jButton.setForeground(getColor(element.getAttribute("foreground")));
        if (element.hasAttribute("background"))
            jButton.setBackground(getColor(element.getAttribute("background")));
        return jButton;
    }

    public static JCheckBox makeJCheckbox(Element element) {
        JCheckBox checkBox = new JCheckBox();
        if (element.hasAttribute("text"))
            checkBox.setText(element.getAttribute("text"));
        if (element.hasAttribute("selected"))
            checkBox.setSelected(Boolean.parseBoolean(element.getAttribute("selected")));
        if (element.hasAttribute("foreground"))
            checkBox.setForeground(getColor(element.getAttribute("foreground")));
        return checkBox;
    }
}
