package comix.sort;

import comix.model.Comix;
import comix.model.PersonalComic;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class sortBySeries implements comixSorter, Comparator<PersonalComic> {
    public static final Comparator<PersonalComic> ORDER = new sortBySeries();

    @Override
    public int compare(PersonalComic first, PersonalComic second) {
        Comix a = first.getComic();
        Comix b = second.getComic();
        int result = a.getSeriesTitle().compareToIgnoreCase(b.getSeriesTitle());
        if (result != 0) {
            return result;
        }
        result = Integer.compare(a.getVolumeNumber(), b.getVolumeNumber());
        if (result != 0) {
            return result;
        }
        result = naturalCompare(a.getIssueNumber(), b.getIssueNumber());
        if (result != 0) {
            return result;
        }
        result = a.getPublisher().compareToIgnoreCase(b.getPublisher());
        if (result != 0) {
            return result;
        }
        return first.getId().compareTo(second.getId());
    }

    // Compare number parts as numbers, so issue 2A comes before issue 10A.
    public static int naturalCompare(String first, String second) {
        int left = 0;
        int right = 0;
        while (left < first.length() && right < second.length()) {
            int leftEnd = endOfPart(first, left);
            int rightEnd = endOfPart(second, right);
            String a = first.substring(left, leftEnd);
            String b = second.substring(right, rightEnd);
            int result;
            if (Character.isDigit(a.charAt(0)) && Character.isDigit(b.charAt(0))) {
                a = withoutLeadingZeros(a);
                b = withoutLeadingZeros(b);
                result = Integer.compare(a.length(), b.length());
                if (result == 0) {
                    result = a.compareTo(b);
                }
            } else {
                result = a.compareToIgnoreCase(b);
            }
            if (result != 0) {
                return result;
            }
            left = leftEnd;
            right = rightEnd;
        }
        if (left < first.length()) {
            return 1;
        }
        if (right < second.length()) {
            return -1;
        }
        return 0;
    }

    private static int endOfPart(String text, int start) {
        boolean number = Character.isDigit(text.charAt(start));
        int end = start + 1;
        while (end < text.length() && Character.isDigit(text.charAt(end)) == number) {
            end++;
        }
        return end;
    }

    private static String withoutLeadingZeros(String number) {
        int start = 0;
        while (start < number.length() - 1 && number.charAt(start) == '0') {
            start++;
        }
        return number.substring(start);
    }

    @Override
    public List<PersonalComic> doSort(List<PersonalComic> comics) {
        List<PersonalComic> result = new ArrayList<>(comics);
        Collections.sort(result, this);
        return result;
    }
}
