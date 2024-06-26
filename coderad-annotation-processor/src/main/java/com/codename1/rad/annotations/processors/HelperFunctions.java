package com.codename1.rad.annotations.processors;

import com.codename1.rad.annotations.Autogenerated;
import org.w3c.dom.*;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.w3c.dom.Node.ELEMENT_NODE;

class HelperFunctions {
    public static String toCamelCase(String str) {
        int lowerCaseIndex = -1;
        int len = str.length();
        for (int i=0; i<len; i++) {
            if (Character.isLowerCase(str.charAt(i))) {
                lowerCaseIndex = i;
                break;
            }
        }
        if(lowerCaseIndex < 0) {
            // No lowercase found.  Change full string to lowercase.
            return str.toLowerCase();
        } else if (lowerCaseIndex == 0) {
            // First character is lower case.  We're good.
            return str;
        } else {
            // First character was capital but next was lowercase.  Just lc the first char.
            return str.substring(0, lowerCaseIndex).toLowerCase() + str.substring(lowerCaseIndex);
        }
    }

    /**
     * Normalizes fully-qualified class name for output in files.  Also strips "stubs." prefix which is used for storing
     * intermediate stub classes for the two pass generation method.
     * @param qualifiedName The qualified name for a class.
     * @return
     */
    static String _(String qualifiedName) {
        if (qualifiedName == null) return "";
        //if (qualifiedName.startsWith(STUBS_PREFIX)) {
        //    return qualifiedName.substring(qualifiedName.indexOf(".")+1);
        //}
        return qualifiedName;
    }

    static String reformat(String content, int indentLevel) {
        int oldLevel = getIndentLevel(content);
        StringBuilder sb = new StringBuilder();
        Scanner scanner = new Scanner(content);
        int delta = indentLevel - oldLevel;
        String lineSeparator = getLineSeparator(content);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            if (delta > 0) {
                for (int i=0; i<delta; i++) {
                    sb.append(' ');
                }
                sb.append(line);
                sb.append(lineSeparator);
            } else {
                int lineIndent = getIndentLevel(line);
                if (lineIndent + delta >= 0) {
                    sb.append(line.substring(-delta));
                } else {
                    sb.append(line.substring(lineIndent));
                }
                sb.append(lineSeparator);
            }
        }
        return sb.toString();
    }

    static int getIndentLevel(String content) {
        Scanner scanner = new Scanner(content);
        outer: while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.trim().isEmpty()) continue;
            int count = 0;
            int len = line.length();
            loop: for (int i=0; i<len; i++) {
                char c = line.charAt(i);
                switch (c) {
                    case ' ': count++; break;
                    case '\t': count++; break;
                    case '\n': continue outer;
                    default: break loop;
                }
            }
            return count;
        }

        return 0;
    }

    static String getLineSeparator(String content) {
        if (content.contains("\r\n")) {
            return "\r\n";
        } else if (content.contains("\n")) {
            return "\n";
        } else {
            return System.lineSeparator();
        }
    }

    /**
     * Extracts the indexed parameters from element.  Indexed parameter are specified both by attributes
     * of the form _N_="..." where N is an integer, and via child elements with attribute rad-property="N" where N is an
     * integer.  These parameters are used when calling the constructor that is created by this tag.
     * @param element An element to check.
     * @return
     */
     static Set<Integer> extractIndexedParameters(org.w3c.dom.Element element) {
        Set<Integer> out = new HashSet<Integer>();
        forEachAttribute(element, attr -> {
            String name = attr.getName();
            if (name.startsWith("_") && name.endsWith("_") && name.length() > 2 && Character.isDigit(name.charAt(1))) {
                try {
                    out.add(Integer.parseInt(name.substring(1, name.length() - 1)));
                } catch (NumberFormatException ex){}
            }
            return null;
        });
        forEachChild(element, child -> {
            if (child.hasAttribute("rad-property")) {
                String name = child.getAttribute("rad-property");
                if (name.length() > 0 && Character.isDigit(name.charAt(0))) {
                    try {
                        out.add(Integer.parseInt(name));
                    } catch (NumberFormatException ex){}
                }
            }
            return null;
        });
        return out;
    }


    static boolean isElementInsideRowTemplate(org.w3c.dom.Element el) {
        if (el.getTagName().equalsIgnoreCase("row-template")) {
            return true;
        }
        Node parentNode = el.getParentNode();
        if (parentNode instanceof org.w3c.dom.Element) {
            org.w3c.dom.Element parentEl = (org.w3c.dom.Element)parentNode;
            return isElementInsideRowTemplate(parentEl);
        }
        return false;
    }

     static StringBuilder indent(StringBuilder sb, int num) {
        for (int i=0; i<num; i++) {
            sb.append(' ');
        }
        return sb;
    }

    static void forEach(org.w3c.dom.Element root, Function<Element, Void> callback) {
        callback.apply(root);
        NodeList children = root.getChildNodes();
        int len = children.getLength();
        for (int i=0; i<len; i++) {
            Node n = (Node)children.item(i);
            if (n instanceof org.w3c.dom.Element) {
                org.w3c.dom.Element childEl = (org.w3c.dom.Element)n;
                forEach(childEl, callback);
            }
        }

    }


    static void forEachChild(org.w3c.dom.Element root, Function<org.w3c.dom.Element, Void> callback) {
        NodeList children = root.getChildNodes();
        int len = children.getLength();
        for (int i=0; i<len; i++) {
            Node n = (Node)children.item(i);
            if (n instanceof org.w3c.dom.Element) {

                org.w3c.dom.Element childEl = (org.w3c.dom.Element)n;
                callback.apply(childEl);
            }
        }

    }

    static List<Element> getChildElements(org.w3c.dom.Element root) {
        List<org.w3c.dom.Element> out = new ArrayList<Element>();
        NodeList children = root.getChildNodes();
        int len = children.getLength();
        for (int i=0; i<len; i++) {
            Node n = (Node)children.item(i);
            if (n.getNodeType() == ELEMENT_NODE) {

                org.w3c.dom.Element childEl = (org.w3c.dom.Element)n;
                out.add(childEl);
            }
        }
        return out;
    }

    static String getTextContent(org.w3c.dom.Element root) {
        if (getDescendantTextContent(root).isEmpty()) {
            return root.getTextContent().trim();
        } else {
            return "";
        }
    }

    static String getDescendantTextContent(org.w3c.dom.Element root) {
        StringBuilder out = new StringBuilder();
        NodeList children = root.getChildNodes();
        int len = children.getLength();
        for (int i=0; i<len; i++) {
            Node n = (Node)children.item(i);
            if (n.getNodeType() == ELEMENT_NODE) {
                out.append(n.getTextContent()).append(" ");
            }
        }
        return out.toString().trim();
    }


    static List<org.w3c.dom.Element> getDescendantElements(List<org.w3c.dom.Element> out, org.w3c.dom.Element root) {
        NodeList children = root.getChildNodes();
        int len = children.getLength();
        for (int i=0; i<len; i++) {
            Node n = (Node)children.item(i);
            if (n.getNodeType() == ELEMENT_NODE) {

                org.w3c.dom.Element childEl = (org.w3c.dom.Element)n;
                out.add(childEl);
                getDescendantElements(out, childEl);
            }
        }
        return out;
    }



    static List<org.w3c.dom.Element> getChildElementsByTagName(org.w3c.dom.Element root, String tagName) {
        return getChildElements(root).stream().filter(e -> e.getTagName().equalsIgnoreCase(tagName)).collect(Collectors.toList());
    }

    static List<org.w3c.dom.Element> getDescendantElementsByTagName(org.w3c.dom.Element root, String tagName) {
        return getDescendantElements(new ArrayList<>(), root).stream().filter(e -> e.getTagName().equalsIgnoreCase(tagName)).collect(Collectors.toList());
    }


    static org.w3c.dom.Element getChildElementByTagName(org.w3c.dom.Element root, String tagName) {
        for (org.w3c.dom.Element child : getChildElements(root)) {
            if (child.getTagName().equals(tagName)) return child;
        }
        return null;
    }

    static void forEachAttribute(org.w3c.dom.Element el, Function<org.w3c.dom.Attr, Void> callback) {
        NamedNodeMap attributes = el.getAttributes();
        int len = attributes.getLength();
        for (int i=0; i<len; i++) {
            Attr attr = (Attr)attributes.item(i);
            callback.apply(attr);
        }
    }


    static String getSimpleName(javax.lang.model.element.Element e) {
        return e.getSimpleName().toString();
    }

    static String[] mergeUnique(String[] s1, String[] s2) {
        List<String> out = new ArrayList<String>(s1.length + s2.length);
        for (String s : s1) {
            if (!out.contains(s)) out.add(s);
        }
        for (String s : s2) {
            if (!out.contains(s)) out.add(s);
        }
        return out.toArray(new String[out.size()]);
    }

    static String getPropName(String methodName) {
        if (methodName.startsWith("get") || methodName.startsWith("set")) {
            return methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
        } else if (methodName.startsWith("is")) {
            return methodName.substring(2, 3).toLowerCase() + methodName.substring(3);
        }
        return null;
    }

    static boolean isAutogenerated(TypeElement el) {
        return el.getAnnotation(Autogenerated.class) != null;
    }

    static boolean isPrimitive(TypeMirror el) {
        switch (el.getKind()) {
            case BOOLEAN:
            case FLOAT:
            case DOUBLE:
            case INT:
            case LONG:
            case CHAR:
            case SHORT:
            case BYTE:
                return true;
        }
        return false;
    }


    static void touch(final Path path) throws IOException {
        if (path == null) throw new IllegalArgumentException("touch path is null");
        if (Files.exists(path)) {
            Files.setLastModifiedTime(path, FileTime.from(Instant.now()));
        } else {
            Files.createFile(path);
        }
    }

    static File findPom(File startingPoint) {
        if (startingPoint == null) return null;
        if (startingPoint.isDirectory()) {
            File pom = new File(startingPoint, "pom.xml");
            if (pom.exists()) return pom;
            return findPom(startingPoint.getParentFile());
        } else {
            return findPom(startingPoint.getParentFile());
        }
    }

    static File getCodenameOneSettingProperties() throws IOException {
        File rootDirectory = findPom(new File(System.getProperty("user.dir"))).getParentFile();
        File cn1Settings = new File(rootDirectory, "codenameone_settings.properties");
        if (!cn1Settings.exists()) {
            cn1Settings = new File(rootDirectory, "common" + File.separator + cn1Settings.getName());
        }
        if (!cn1Settings.exists()) {
            cn1Settings = new File(rootDirectory.getParentFile(), "common" + File.separator + cn1Settings.getName());
        }
        if (!cn1Settings.exists()) {
            cn1Settings = new File(rootDirectory.getParentFile(), cn1Settings.getName());
        }

        if (!cn1Settings.exists()) {
            throw new IOException("Cannot find Codename One project directory in which to generate XML schemas.");
        }
        return cn1Settings;
    }


    static boolean fileContainsString(String needle, File haystack) throws IOException {
        List<String> wholeData = Files.readAllLines(haystack.toPath());

        for (String wholeDatum : wholeData) {
            if (wholeDatum.contains(needle))
                return true;
        }
        return false;
    }

}
