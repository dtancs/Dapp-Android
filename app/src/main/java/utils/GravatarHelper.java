package utils;

/**
 * Created by tancs on 4/29/17.
 */

public final class GravatarHelper{

    public static String getImageURL(String email){

        String output = "https://www.gravatar.com/avatar/";

        output += MD5(email);
        output += "?d=identicon";

        return output;
    }

    public static String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }
}