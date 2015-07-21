package com.ljmob.richeditorfragment;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import jp.wasabeef.richeditor.RichEditor;

/**
 * Created by london on 15/7/8.
 * RichEditorFragment
 */
public class RichEditorFragment extends Fragment {
    View rootView;
    ImageButton action_undo;
    ImageButton action_redo;
    ImageButton action_bold;
    ImageButton action_italic;
    ImageButton action_subscript;
    ImageButton action_superscript;
    ImageButton action_strike_through;
    ImageButton action_underline;
    ImageButton action_heading1;
    ImageButton action_heading2;
    ImageButton action_heading3;
    ImageButton action_heading4;
    ImageButton action_heading5;
    ImageButton action_heading6;
    ImageButton action_text_color;
    ImageButton action_bg_color;
    ImageButton action_indent;
    ImageButton action_out_dent;
    ImageButton action_align_left;
    ImageButton action_align_center;
    ImageButton action_align_right;
    ImageButton action_block_quote;
    ImageButton action_insert_image;
    ImageButton action_insert_link;
    jp.wasabeef.richeditor.RichEditor editor;
    TextView preview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }
//        rootView = inflater.inflate(R.layout.view_test, container, false);
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        initView(rootView);
        return rootView;
    }

    private void initView(View root) {
        action_undo = (ImageButton) root.findViewById(R.id.action_undo);
        action_redo = (ImageButton) root.findViewById(R.id.action_redo);
        action_bold = (ImageButton) root.findViewById(R.id.action_bold);
        action_italic = (ImageButton) root.findViewById(R.id.action_italic);
        action_subscript = (ImageButton) root.findViewById(R.id.action_subscript);
        action_superscript = (ImageButton) root.findViewById(R.id.action_superscript);
        action_strike_through = (ImageButton) root.findViewById(R.id.action_strike_through);
        action_underline = (ImageButton) root.findViewById(R.id.action_underline);
        action_heading1 = (ImageButton) root.findViewById(R.id.action_heading1);
        action_heading2 = (ImageButton) root.findViewById(R.id.action_heading2);
        action_heading3 = (ImageButton) root.findViewById(R.id.action_heading3);
        action_heading4 = (ImageButton) root.findViewById(R.id.action_heading4);
        action_heading5 = (ImageButton) root.findViewById(R.id.action_heading5);
        action_heading6 = (ImageButton) root.findViewById(R.id.action_heading6);
        action_text_color = (ImageButton) root.findViewById(R.id.action_txt_color);
        action_bg_color = (ImageButton) root.findViewById(R.id.action_bg_color);
        action_indent = (ImageButton) root.findViewById(R.id.action_indent);
        action_out_dent = (ImageButton) root.findViewById(R.id.action_out_dent);
        action_align_left = (ImageButton) root.findViewById(R.id.action_align_left);
        action_align_center = (ImageButton) root.findViewById(R.id.action_align_center);
        action_align_right = (ImageButton) root.findViewById(R.id.action_align_right);
        action_block_quote = (ImageButton) root.findViewById(R.id.action_block_quote);
        action_insert_image = (ImageButton) root.findViewById(R.id.action_insert_image);
        action_insert_link = (ImageButton) root.findViewById(R.id.action_insert_link);
        editor = (jp.wasabeef.richeditor.RichEditor) root.findViewById(R.id.editor);
        preview = (TextView) root.findViewById(R.id.preview);

        editor.setOnTextChangeListener(new RichEditor.OnTextChangeListener() {
            @Override
            public void onTextChange(String text) {
                preview.setText(text);
            }
        });
        initButtonListener();
    }

    private void initButtonListener() {
        action_undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.undo();
            }
        });

        action_redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.redo();
            }
        });

        action_bold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.setBold();
            }
        });

        action_italic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.setItalic();
            }
        });


        action_subscript.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.setSubscript();
            }
        });

        action_superscript.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.setSuperscript();
            }
        });

        action_strike_through.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.setStrikeThrough();
            }
        });

        action_underline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.setUnderline();
            }
        });

        action_heading1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.setHeading(1);
            }
        });

        action_heading2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.setHeading(2);
            }
        });

        action_heading3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.setHeading(3);
            }
        });

        action_heading4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.setHeading(4);
            }
        });

        action_heading5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.setHeading(5);
            }
        });

        action_heading6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.setHeading(6);
            }
        });

        action_text_color.setOnClickListener(new View.OnClickListener() {
            boolean isChanged;

            @Override
            public void onClick(View v) {
                editor.setTextColor(isChanged ? Color.BLACK : Color.RED);
                isChanged = !isChanged;
            }
        });

        action_bg_color.setOnClickListener(new View.OnClickListener() {
            boolean isChanged;

            @Override
            public void onClick(View v) {
                editor.setTextBackgroundColor(isChanged ? Color.TRANSPARENT : Color.YELLOW);
                isChanged = !isChanged;
            }
        });

        action_indent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.setIndent();
            }
        });

        action_out_dent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.setOutdent();
            }
        });

        action_align_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.setAlignLeft();
            }
        });

        action_align_center.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.setAlignCenter();
            }
        });

        action_align_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.setAlignRight();
            }
        });

        action_block_quote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.setBlockquote();
            }
        });

        action_insert_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.insertImage(
                        "http://www.1honeywan.com/dachshund/image/7.21/7.21_3_thumb.JPG",
                        "dachshund");
            }
        });
    }
}
