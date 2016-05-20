package com.ljmob.lemoji;

import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.ljmob.lemoji.entity.Emoji;
import com.londonx.lutil.Lutil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by london on 15/7/30.
 * Tools of emoji
 */
public class LEmoji {
    private static List<Emoji> emojiList;

    public static List<Emoji> getAllEmoji() {
        if (emojiList != null && emojiList.size() != 0) {
            return emojiList;
        }
        emojiList = new ArrayList<>();
        AssetManager assetManager = Lutil.context.getAssets();
        String[] strings = new String[0];
        try {
            strings = assetManager.list("emoji");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (strings.length == 0) {
            return emojiList;
        }
        for (int i = 0; i < strings.length; i++) {
            String str = strings[i];
            Emoji emoji = new Emoji();
            emoji.id = i;
            emoji.tag = Emoji.START + str + Emoji.END;
            try {
                emoji.picture = BitmapFactory.decodeStream(assetManager.open("emoji/" + str));
            } catch (IOException e) {
                e.printStackTrace();
            }
            emojiList.add(emoji);
        }
        return emojiList;
    }

    public static SpannableString translate(final String origin) {
        if (emojiList == null || emojiList.size() == 0) {
            getAllEmoji();
        }
//      SpannableString 给TextView加上特殊的文本效果
        SpannableString spannableString = new SpannableString(origin);
        List<int[]> indexList = new ArrayList<>();
        int lastIndexOfEnd = 0;
        while (true) {
            int startIndex = origin.indexOf(Emoji.START, lastIndexOfEnd);
            if (startIndex == -1) {
                break;
            }
            int endIndex = origin.indexOf(Emoji.END, startIndex) + Emoji.END.length();
            int[] sequence = new int[2];
            sequence[0] = startIndex;
            sequence[1] = endIndex;
            indexList.add(sequence);
            lastIndexOfEnd = endIndex;
        }
        for (int[] sequence : indexList) {
            String tag = origin.substring(sequence[0], sequence[1]);
            for (Emoji selectEmoji : emojiList) {
                if (tag.equals(selectEmoji.tag)) {
                    ImageSpan imageSpan = new ImageSpan(Lutil.context, selectEmoji.picture);
                    spannableString.setSpan(imageSpan, sequence[0], sequence[1],
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                }
            }
        }
        return spannableString;
    }

    public static String simplify(final String origin) {
        String simpleString = origin;
        while (true) {
            int startIndex = simpleString.indexOf(Emoji.START);
            if (startIndex == -1) {
                break;
            }
            int endIndex = simpleString.indexOf(Emoji.END, startIndex);
            if (endIndex != -1) {
                endIndex += Emoji.END.length();
            } else {
                endIndex = simpleString.length();
            }

            String tag = simpleString.substring(startIndex, endIndex);
            simpleString = simpleString.replace(tag, "[表情]");
        }
        return simpleString;
    }
}
