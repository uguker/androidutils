package com.cqray.android.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.cqray.android.R;

/**
 * 功能描述：输入文本控件
 * @author LeiJue
 */
public class SimpleInputLayout extends RelativeLayout {

    private boolean mPassword;
    private boolean mPasswordVisible;
    private boolean mInputCenter;

    private EditText mEditView;
    private ImageView deleteView;
    private ImageView toggleView;

    private float mInputPadding;

    private ColorStateList mTextColor;
    private ColorStateList mHintColor;

    private Drawable mInputIcon;
    private Drawable mInputDelete;
    private Drawable mInputToggleVisible;
    private Drawable mInputToggleInvisible;

    private OnFocusChangeListener mChangeListener;
    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.android_input_delete) {
                mEditView.setText(null);
            } else if (id == R.id.android_input_toggle){
                mPasswordVisible = !mPasswordVisible;
                mEditView.setTransformationMethod(mPasswordVisible ? null : PasswordTransformationMethod.getInstance());
                mEditView.setSelection(mEditView.getSelectionEnd());

                toggleView.setImageResource(mPasswordVisible ?
                        R.drawable.widget_input_visible :
                        R.drawable.widget_input_invisible);
            }
        }
    };

    public SimpleInputLayout(Context context) {
        this(context, null);
    }

    public SimpleInputLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("CustomViewStyleable")
    public SimpleInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


        float density = getResources().getDisplayMetrics().density;
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SimpleInputLayout);
        String hint = a.getString(R.styleable.SimpleInputLayout_inputHint);
        mTextColor = a.getColorStateList(R.styleable.SimpleInputLayout_inputTextColor);
        mHintColor = a.getColorStateList(R.styleable.SimpleInputLayout_inputHintColor);
        mInputPadding = a.getDimensionPixelSize(R.styleable.SimpleInputLayout_inputPadding, (int) (8 * density));
        int inputType = a.getInt(R.styleable.SimpleInputLayout_inputType, 0);
        int inputMaxLength = a.getInt(R.styleable.SimpleInputLayout_inputMaxLength, 0);
        float textSize = a.getDimension(R.styleable.SimpleInputLayout_inputTextSize, density * 16);
        mInputCenter =  a.getBoolean(R.styleable.SimpleInputLayout_inputCenter, false);
        mInputIcon = a.getDrawable(R.styleable.SimpleInputLayout_inputIcon);
        mInputDelete = a.getDrawable(R.styleable.SimpleInputLayout_inputDeleteIcon);
        mInputToggleVisible = a.getDrawable(R.styleable.SimpleInputLayout_inputToggleVisibleIcon);
        mInputToggleInvisible = a.getDrawable(R.styleable.SimpleInputLayout_inputToggleVisibleIcon);
        mPassword = inputType > 1;
        a.recycle();

        if (mInputDelete == null) {
            mInputDelete = ContextCompat.getDrawable(getContext(), R.drawable.widget_input_delete);
        }
        if (mInputToggleVisible == null) {
            mInputToggleVisible = ContextCompat.getDrawable(getContext(), R.drawable.widget_input_visible);
        }
        if (mInputToggleInvisible == null) {
            mInputToggleInvisible = ContextCompat.getDrawable(getContext(), R.drawable.widget_input_invisible);
        }

        initView();
        mEditView.setHint(hint);
        mEditView.setInputType(inputType);
        mEditView.setTextSize(textSize / density);
        mEditView.setTextColor(mTextColor != null ? mTextColor : ColorStateList.valueOf(0xFF000000));
        mEditView.setHintTextColor(mHintColor != null ? mHintColor : ColorStateList.valueOf(0xFF000000));

        if (inputMaxLength != 0) {
            mEditView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(inputMaxLength)});
        }

        switch (inputType) {
            case 0:
                mEditView.setInputType(EditorInfo.TYPE_CLASS_TEXT);
//                mEditView.setKeyListener(DigitsKeyListener.getInstance(
//                        "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789@._"));
                break;
            case 1:
                mEditView.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
                break;
            default:
                mEditView.setInputType(EditorInfo.TYPE_CLASS_TEXT |
                        EditorInfo.TYPE_TEXT_VARIATION_PASSWORD );
//                mEditView.setKeyListener(DigitsKeyListener.getInstance(
//                        "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789@._"));
        }
        if (mInputIcon != null) {
            mInputIcon.setBounds(0, 0, mInputIcon.getMinimumWidth(), mInputIcon.getMinimumHeight());
        }
        // 设置图标
        mEditView.setCompoundDrawables(
                mInputIcon,
                mEditView.getCompoundDrawables()[1],
                mEditView.getCompoundDrawables()[2],
                mEditView.getCompoundDrawables()[3]);
    }


    @SuppressLint("ResourceType")
    private void initView() {
        LayoutParams params;

        params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        int padding = (int) (mInputPadding * 3 + mInputDelete.getIntrinsicWidth() +
                (mPassword ? mInputDelete.getIntrinsicWidth(): 0));
        mEditView = new AppCompatEditText(getContext());
        mEditView.setLayoutParams(params);
        mEditView.setLayoutParams(params);
        mEditView.setCompoundDrawablePadding((int) mInputPadding);
        mEditView.setPadding(mInputCenter ?  padding : (int) (mInputPadding / 2), 0, padding, 0);
        mEditView.setGravity(mInputCenter ? Gravity.CENTER : Gravity.START | CENTER_VERTICAL);
        addView(mEditView);

        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.rightMargin = (int) (mInputPadding / 2);
        toggleView = new AppCompatImageView(getContext());
        toggleView.setId(R.id.android_input_toggle);
        toggleView.setLayoutParams(params);
        toggleView.setPadding((int) (mInputPadding / 4), (int) (mInputPadding / 4),
                (int) (mInputPadding / 4), (int) (mInputPadding / 4));
        toggleView.setImageResource(R.drawable.widget_input_invisible);
        toggleView.setClickable(true);
        // 设置背景
        TypedArray array = getContext().obtainStyledAttributes(new int[] {android.R.attr.selectableItemBackground});
        Drawable drawable = array.getDrawable(0);
        array.recycle();
        ViewCompat.setBackground(toggleView, drawable);

        addView(toggleView);

        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.addRule(RelativeLayout.LEFT_OF, R.id.android_input_toggle);
        deleteView = new AppCompatImageView(getContext());
        deleteView.setId(R.id.android_input_delete);
        deleteView.setLayoutParams(params);
        deleteView.setImageResource(R.drawable.widget_input_delete);
        deleteView.setPadding((int) (mInputPadding / 4), (int) (mInputPadding / 4),
                (int) (mInputPadding / 4), (int) (mInputPadding / 4));
        deleteView.setClickable(true);
        addView(deleteView);

        mEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean empty = TextUtils.isEmpty(s);
                deleteView.setVisibility(empty || !mEditView.hasFocus() ? GONE : VISIBLE);
                toggleView.getLayoutParams().width = !empty && mPassword ?
                        LayoutParams.WRAP_CONTENT : 0;
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        mEditView.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !TextUtils.isEmpty(mEditView.getText())) {
                    deleteView.setVisibility(VISIBLE);
                } else {
                    deleteView.setVisibility(GONE);
                }
                if (mChangeListener != null) {
                    mChangeListener.onFocusChange(v, hasFocus);
                }
            }
        });

        deleteView.setOnClickListener(mClickListener);
        toggleView.setOnClickListener(mClickListener);
    }

    public SimpleInputLayout setHint(CharSequence hint) {
        mEditView.setHint(hint);
        return this;
    }

    public SimpleInputLayout setText(CharSequence text) {
        mEditView.setText(text);
        return this;
    }

    public SimpleInputLayout setOnTextFocusChangeListener(OnFocusChangeListener listener) {
        mChangeListener = listener;
        return this;
    }

    public SimpleInputLayout addTextChangedListener(TextWatcher watcher) {
        mEditView.addTextChangedListener(watcher);
        return this;
    }

    public Editable getText() {
        return mEditView.getText();
    }

}
