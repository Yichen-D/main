package seedu.taskmaster.storage;

import seedu.taskmaster.commons.exceptions.DataConversionException;
import seedu.taskmaster.commons.util.XmlUtil;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * Stores task list data in an XML file
 */
public class XmlFileStorage {
    /**
     * Saves the given task list data to the specified file.
     */
    public static void saveDataToFile(File file, XmlSerializableTaskList taskList)
            throws FileNotFoundException {
        try {
            XmlUtil.saveDataToFile(file, taskList);
        } catch (JAXBException e) {
            assert false : "Unexpected exception " + e.getMessage();
        }
    }

    /**
     * Returns address book in the file or an empty address book
     */
    public static XmlSerializableTaskList loadDataFromSaveFile(File file) throws DataConversionException,
                                                                            FileNotFoundException {
        try {
            return XmlUtil.getDataFromFile(file, XmlSerializableTaskList.class);
        } catch (JAXBException e) {
            throw new DataConversionException(e);
        }
    }

}
