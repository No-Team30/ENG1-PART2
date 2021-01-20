package tools;

import org.json.simple.JSONObject;

public class LoadData {

    /**
     * Attempts to load the given parameterName, from the JSONFILE
     * Throws IllegalArgumentException, if it is null, not a string or does not match the expected value
     *
     * @param object        The JSONObject to load the value from
     * @param parameterName The name of the parameter to load from the file
     * @param expectedValue What the value should equal
     * @return The value retrieved from the JSONObject (this also should equal the expectedValue)
     */
    public static <T> T validateAndLoadObject(JSONObject object, String parameterName,
                                              T expectedValue) {
        T parameter = (T) loadObject(object, parameterName, expectedValue.getClass());
        if (!parameter.equals(expectedValue)) {
            throw new IllegalArgumentException("Parameter (" + parameterName + ") does not equal (" +
                    expectedValue + ")");
        }
        return parameter;
    }

    /**
     * Attempts to load the given parameterName, from the JSONFILE
     * Throws IllegalArgumentException, if the value is null, or does not match the expected type
     *
     * @param object        The JSONObject to load the value from
     * @param parameterName The name of the parameter to load from the file
     * @return The value retrieved from the JSONObject
     */
    public static <T> T loadObject(JSONObject object, String parameterName, Class<T> type) {
        Object parameter = object.get(parameterName);
        if (parameter == null) {
            throw new IllegalArgumentException("Parameter (" + parameterName + ") does not exist in JSON Object");
        }
        if (!(type.isInstance(parameter))) {
            throw new IllegalArgumentException("Parameter: " + parameterName + " is of incorrect type. Expected: " + type +
                    " Received: " + parameter.getClass());
        }
        return (T) parameter;
    }
}
