package ja.burhanrashid52.photoeditor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.annotation.UiThread;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * This class in initialize by {@link PhotoEditor.Builder} using a builder pattern with multiple
 * editing attributes
 * </p>
 *
 * @author <a href="https://github.com/burhanrashid52">Burhanuddin Rashid</a>
 * @version 0.1.1
 * @since 18/01/2017
 */
public class PhotoEditor implements BrushViewChangeListener {

    private static final String TAG = "PhotoEditor";
    private final LayoutInflater mLayoutInflater;
    private Context context;
    private PhotoEditorView parentView;
    private ImageView imageView;
    private View deleteView;
    private BrushDrawingView brushDrawingView;
    private List<View> addedViews;
    private List<View> redoViews;
    private OnPhotoEditorListener mOnPhotoEditorListener;
    private boolean isTextPinchZoomable;
    private Typeface mDefaultTextTypeface;
    private Typeface mDefaultEmojiTypeface;

    private PhotoEditor(Builder builder) {
        this.context = builder.context;
        this.parentView = builder.parentView;
        this.imageView = builder.imageView;
        this.deleteView = builder.deleteView;
        this.brushDrawingView = builder.brushDrawingView;
        this.isTextPinchZoomable = builder.isTextPinchZoomable;
        this.mDefaultTextTypeface = builder.textTypeface;
        this.mDefaultEmojiTypeface = builder.emojiTypeface;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        brushDrawingView.setBrushViewChangeListener(this);
        addedViews = new ArrayList<>();
        redoViews = new ArrayList<>();
    }

    public interface OnHandlerEventEditor {
        void onMoveEventListener(View view, ViewType viewType);
    }

    /**
     * This will add image on {@link PhotoEditorView} which you drag,rotate and scale using pinch
     * if {@link PhotoEditor.Builder#setPinchTextScalable(boolean)} enabled
     *
     * @param desiredImage bitmap image you want to add
     */
    public void addImage(Bitmap desiredImage) {
        final View imageRootView = getLayout(ViewType.IMAGE);
        final ImageView imageView = imageRootView.findViewById(R.id.imgPhotoEditorImage);
        final FrameLayout frmBorder = imageRootView.findViewById(R.id.frmBorder);
        final ImageView imgClose = imageRootView.findViewById(R.id.imgPhotoEditorClose);

        imageView.setImageBitmap(desiredImage);

        MultiTouchListener multiTouchListener = getMultiTouchListener();
        multiTouchListener.setOnGestureControl(new MultiTouchListener.OnGestureControl() {
            @Override
            public void onClick() {
                boolean isBackgroundVisible = frmBorder.getTag() != null && (boolean) frmBorder.getTag();
                frmBorder.setBackgroundResource(isBackgroundVisible ? 0 : R.drawable.rounded_border_tv);
                imgClose.setVisibility(isBackgroundVisible ? View.INVISIBLE : View.VISIBLE);
                frmBorder.setTag(!isBackgroundVisible);
            }

            @Override
            public void onLongClick() {

            }

            @Override
            public void onTransform(float x, float y, float scaleX, float scaleY, float rotation, int width, int height) {

            }
        });

        imageRootView.setOnTouchListener(multiTouchListener);

        addViewToParent(imageRootView, ViewType.IMAGE);

    }

    public void editImage(@NonNull View view, String url) {
        final ImageView imageView = view.findViewById(R.id.imgPhotoEditorImage);
        if (addedViews.contains(view) && !TextUtils.isEmpty(url)) {
            Glide.with(context).load(url)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .diskCacheStrategy(DiskCacheStrategy.ALL).dontTransform()
                    .dontAnimate().override(300).into(imageView);

            parentView.updateViewLayout(view, view.getLayoutParams());
            int i = addedViews.indexOf(view);
            if (i > -1) addedViews.set(i, view);
        }
    }

    public void addImage(final String url, final OnGetViewRoot onGetViewRoot) {
        final View imageRootView = getLayout(ViewType.IMAGE);
        final ImageView imageView = imageRootView.findViewById(R.id.imgPhotoEditorImage);
        final FrameLayout frmBorder = imageRootView.findViewById(R.id.frmBorder);
        final ImageView imgClose = imageRootView.findViewById(R.id.imgPhotoEditorClose);
        final ImageView imgReset = imageRootView.findViewById(R.id.imgPhotoEditorReset);

        Glide.with(context).load(url)
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL).dontTransform()
                .dontAnimate().override(300).into(imageView);

        MultiTouchListener multiTouchListener = getMultiTouchListener();
        multiTouchListener.setOnGestureControl(new MultiTouchListener.OnGestureControl() {
            @Override
            public void onClick() {
                boolean isBackgroundVisible = frmBorder.getTag() != null && (boolean) frmBorder.getTag();
                frmBorder.setBackgroundResource(isBackgroundVisible ? 0 : R.drawable.rounded_border_tv);
                imgClose.setVisibility(isBackgroundVisible ? View.INVISIBLE : View.VISIBLE);
                imgReset.setVisibility(isBackgroundVisible ? View.INVISIBLE : View.VISIBLE);
                frmBorder.setTag(!isBackgroundVisible);

                onGetViewRoot.onMoveEditorEventListener(imageRootView, ViewType.IMAGE);
            }

            @Override
            public void onLongClick() {
                onGetViewRoot.onMoveEditorEventListener(imageRootView, ViewType.IMAGE);
            }

            @Override
            public void onTransform(float x, float y, float scaleX, float scaleY, float rotation, int width, int height) {
                if (mOnPhotoEditorListener != null) {
                    mOnPhotoEditorListener.onViewTransform(ViewType.IMAGE, imageRootView, x, y, scaleX, scaleY, rotation, width, height);
                }

                onGetViewRoot.onMoveEditorEventListener(imageRootView, ViewType.IMAGE);
            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnPhotoEditorListener != null) {
                    mOnPhotoEditorListener.onRemoveViewListener(ViewType.IMAGE, imageRootView, addedViews.size());
                }
                try {
                    addedViews.remove(imageRootView);
                    parentView.removeView(imageRootView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        imgReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageRootView.setRotation(0.0f);
            }
        });

        imageRootView.setOnTouchListener(multiTouchListener);
        addViewToParent(imageRootView, ViewType.IMAGE);
        onGetViewRoot.onGetView(imageRootView);
    }

    /**
     * This add the text on the {@link PhotoEditorView} with provided parameters
     * by default {@link TextView#setText(int)} will be 18sp
     *
     * @param text              text to display
     * @param colorCodeTextView text color to be displayed
     */
    @SuppressLint("ClickableViewAccessibility")
    public void addText(String text, final int colorCodeTextView) {
        addText(null, text, colorCodeTextView);
    }

    /**
     * This add the text on the {@link PhotoEditorView} with provided parameters
     * by default {@link TextView#setText(int)} will be 18sp
     *
     * @param textTypeface      typeface for custom font in the text
     * @param text              text to display
     * @param colorCodeTextView text color to be displayed
     */
    @SuppressLint("ClickableViewAccessibility")
    public void addText(@Nullable Typeface textTypeface, String text, final int colorCodeTextView) {
        final TextStyleBuilder styleBuilder = new TextStyleBuilder();

        styleBuilder.withTextColor(colorCodeTextView);
        if (textTypeface != null) {
            styleBuilder.withTextFont(textTypeface);
        }

        addText(text, styleBuilder);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void addText(@Nullable Typeface textTypeface, int shadow, int gravity, float sizes, String text, final int colorCodeTextView, int paintFlags, OnGetViewRoot onGetViewRoot) {
        final TextStyleBuilder styleBuilder = new TextStyleBuilder();

        styleBuilder.withTextColor(colorCodeTextView);
        if (textTypeface != null) {
            styleBuilder.withTextFont(textTypeface);
        }

        addText(text, shadow, gravity, sizes, styleBuilder, paintFlags, onGetViewRoot);
    }

    /**
     * This add the text on the {@link PhotoEditorView} with provided parameters
     * by default {@link TextView#setText(int)} will be 18sp
     *
     * @param text         text to display
     * @param styleBuilder text style builder with your style
     */
    @SuppressLint("ClickableViewAccessibility")
    public void addText(String text, @Nullable TextStyleBuilder styleBuilder) {
        brushDrawingView.setBrushDrawingMode(false);
        final View textRootView = getLayout(ViewType.TEXT);
        final TextView textInputTv = textRootView.findViewById(R.id.tvPhotoEditorText);
        final ImageView imgClose = textRootView.findViewById(R.id.imgPhotoEditorClose);
        final ImageView imgEdit = textRootView.findViewById(R.id.imgPhotoEditorEdit);
        final ImageView imgReset = textRootView.findViewById(R.id.imgPhotoEditorReset);

        final FrameLayout frmBorder = textRootView.findViewById(R.id.frmBorder);

        textInputTv.setText(text);
        if (styleBuilder != null) styleBuilder.applyStyle(textInputTv);

        MultiTouchListener multiTouchListener = getMultiTouchListener();
        multiTouchListener.setOnGestureControl(new MultiTouchListener.OnGestureControl() {
            @Override
            public void onClick() {
                boolean isBackgroundVisible = frmBorder.getTag() != null && (boolean) frmBorder.getTag();
                frmBorder.setBackgroundResource(isBackgroundVisible ? 0 : R.drawable.rounded_border_tv);
                imgEdit.setVisibility(isBackgroundVisible ? View.INVISIBLE : View.VISIBLE);
                imgClose.setVisibility(isBackgroundVisible ? View.INVISIBLE : View.VISIBLE);
                imgReset.setVisibility(isBackgroundVisible ? View.INVISIBLE : View.VISIBLE);
                frmBorder.setTag(!isBackgroundVisible);
            }

            @Override
            public void onLongClick() {
//                String textInput = textInputTv.getText().toString();
//                int currentTextColor = textInputTv.getCurrentTextColor();
//                if (mOnPhotoEditorListener != null) {
//                    mOnPhotoEditorListener.onEditTextChangeListener(textRootView, textInputTv, textInput, currentTextColor);
//                }
            }

            @Override
            public void onTransform(float x, float y, float scaleX, float scaleY, float rotation, int width, int height) {
                if (mOnPhotoEditorListener != null) {
                    mOnPhotoEditorListener.onViewTransform(ViewType.TEXT, textInputTv, x, y, scaleX, scaleY, rotation, width, height);
                }
            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnPhotoEditorListener != null) {
                    mOnPhotoEditorListener.onRemoveViewListener(ViewType.TEXT, textInputTv, addedViews.size());
                }
                try {
                    addedViews.remove(textRootView);
                    parentView.removeView(textRootView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textInput = textInputTv.getText().toString();
                int currentTextColor = textInputTv.getCurrentTextColor();
                if (mOnPhotoEditorListener != null) {
                    mOnPhotoEditorListener.onEditTextChangeListener(textRootView, textInputTv, textInput, currentTextColor);
                }
            }
        });

        imgReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textRootView.setRotation(0.0f);
            }
        });


        textRootView.setOnTouchListener(multiTouchListener);
        addViewToParent(textRootView, ViewType.TEXT);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void addText(String text, int colorCodeTextView, final OnEditorCallBack onEditorCallBack) {
        brushDrawingView.setBrushDrawingMode(false);
        final View textRootView = getLayout(ViewType.TEXT);
        final TextView textInputTv = textRootView.findViewById(R.id.tvPhotoEditorText);
        final ImageView imgClose = textRootView.findViewById(R.id.imgPhotoEditorClose);
        final ImageView imgEdit = textRootView.findViewById(R.id.imgPhotoEditorEdit);
        final ImageView imgReset = textRootView.findViewById(R.id.imgPhotoEditorReset);
        final FrameLayout frmBorder = textRootView.findViewById(R.id.frmBorder);

        textInputTv.setText(text);
        textInputTv.setTextColor(colorCodeTextView);
        textInputTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);


        MultiTouchListener multiTouchListener = getMultiTouchListener();
        multiTouchListener.setOnGestureControl(new MultiTouchListener.OnGestureControl() {
            @Override
            public void onClick() {
                boolean isBackgroundVisible = frmBorder.getTag() != null && (boolean) frmBorder.getTag();
                imgClose.setVisibility(isBackgroundVisible ? View.INVISIBLE : View.VISIBLE);
                imgEdit.setVisibility(isBackgroundVisible ? View.INVISIBLE : View.VISIBLE);
                imgReset.setVisibility(isBackgroundVisible ? View.INVISIBLE : View.VISIBLE);

                frmBorder.setBackgroundResource(isBackgroundVisible ? 0 : R.drawable.rounded_border_tv);
                frmBorder.setTag(!isBackgroundVisible);
            }

            @Override
            public void onLongClick() {
                String textInput = textInputTv.getText().toString();
                int currentTextColor = textInputTv.getCurrentTextColor();
                if (mOnPhotoEditorListener != null) {
                    mOnPhotoEditorListener.onEditTextChangeListener(textRootView, textInputTv, textInput, currentTextColor);
                }
            }

            @Override
            public void onTransform(float x, float y, float scaleX, float scaleY, float rotation, int width, int height) {
                if (mOnPhotoEditorListener != null) {
                    mOnPhotoEditorListener.onViewTransform(ViewType.TEXT, textInputTv, x, y, scaleX, scaleY, rotation, width, height);
                }
            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnPhotoEditorListener != null) {
                    mOnPhotoEditorListener.onRemoveViewListener(ViewType.TEXT, textInputTv, addedViews.size());
                }
                try {
                    addedViews.remove(textRootView);
                    parentView.removeView(textRootView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEditorCallBack.onEditView(textRootView);
            }
        });

        imgReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textRootView.setRotation(0.0f);
            }
        });

        textRootView.setOnTouchListener(multiTouchListener);
        addViewToParent(textRootView, ViewType.TEXT);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addText(String text, int shadowColor, final int gravity, float sizes, @Nullable TextStyleBuilder styleBuilder, int paintFlags, final OnGetViewRoot onGetViewRoot) {
        brushDrawingView.setBrushDrawingMode(false);
        final View textRootView = getLayout(ViewType.TEXT);
        final TextView textInputTv = textRootView.findViewById(R.id.tvPhotoEditorText);
        final ImageView imgClose = textRootView.findViewById(R.id.imgPhotoEditorClose);
        final ImageView imgEdit = textRootView.findViewById(R.id.imgPhotoEditorEdit);
        final ImageView imgReset = textRootView.findViewById(R.id.imgPhotoEditorReset);
        final FrameLayout frmBorder = textRootView.findViewById(R.id.frmBorder);


        textInputTv.setText(text);
        textInputTv.setPaintFlags(paintFlags);
        if (styleBuilder != null) styleBuilder.applyStyle(textInputTv);
        textInputTv.setShadowLayer(2f, 1.5f, 1.5f, shadowColor);
        textInputTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizes);
        textInputTv.setGravity(gravity);

        if (gravity == Gravity.START) {
            frmBorder.setBackgroundResource(R.drawable.rounded_left_border_tv);
        } else if (gravity == Gravity.END) {
            frmBorder.setBackgroundResource(R.drawable.rounded_right_border_tv);
        } else {
            frmBorder.setBackgroundResource(R.drawable.rounded_border_tv);
        }

        MultiTouchListener multiTouchListener = getMultiTouchListener();
        multiTouchListener.setOnGestureControl(new MultiTouchListener.OnGestureControl() {
            @Override
            public void onClick() {
                boolean isBackgroundVisible = frmBorder.getTag() != null && (boolean) frmBorder.getTag();
                if (gravity == Gravity.START) {
                    frmBorder.setBackgroundResource(isBackgroundVisible ? 0 : R.drawable.rounded_left_border_tv);
                } else if (gravity == Gravity.END) {
                    frmBorder.setBackgroundResource(isBackgroundVisible ? 0 : R.drawable.rounded_right_border_tv);
                } else {
                    frmBorder.setBackgroundResource(isBackgroundVisible ? 0 : R.drawable.rounded_border_tv);
                }

                imgClose.setVisibility(isBackgroundVisible ? View.INVISIBLE : View.VISIBLE);
                imgEdit.setVisibility(isBackgroundVisible ? View.INVISIBLE : View.VISIBLE);
                imgReset.setVisibility(isBackgroundVisible ? View.INVISIBLE : View.VISIBLE);
                frmBorder.setTag(!isBackgroundVisible);

                onGetViewRoot.onMoveEditorEventListener(textRootView, ViewType.TEXT);
            }

            @Override
            public void onLongClick() {
                String textInput = textInputTv.getText().toString();
                int currentTextColor = textInputTv.getCurrentTextColor();
                if (mOnPhotoEditorListener != null) {
                    mOnPhotoEditorListener.onEditTextChangeListener(textRootView, textInputTv, textInput, currentTextColor);
                }
                onGetViewRoot.onMoveEditorEventListener(textRootView, ViewType.TEXT);
            }

            @Override
            public void onTransform(float x, float y, float scaleX, float scaleY, float rotation, int width, int height) {
                if (mOnPhotoEditorListener != null) {
                    mOnPhotoEditorListener.onViewTransform(ViewType.TEXT, textInputTv, x, y, scaleX, scaleY, rotation, width, height);
                }
                onGetViewRoot.onMoveEditorEventListener(textRootView, ViewType.TEXT);
            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnPhotoEditorListener != null) {
                    mOnPhotoEditorListener.onRemoveViewListener(ViewType.TEXT, textInputTv, addedViews.size());
                }
                try {
                    addedViews.remove(textRootView);
                    parentView.removeView(textRootView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textInput = textInputTv.getText().toString();
                int currentTextColor = textInputTv.getCurrentTextColor();
                if (mOnPhotoEditorListener != null) {
                    mOnPhotoEditorListener.onEditTextChangeListener(textRootView, textInputTv, textInput, currentTextColor);
                }
            }
        });

        imgReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textRootView.setRotation(0.0f);
            }
        });

        textRootView.setOnTouchListener(multiTouchListener);
        addViewToParent(textRootView, ViewType.TEXT);
        onGetViewRoot.onGetView(textRootView);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void addTextWithFont(@Nullable Typeface textTypeface, String text, final int colorCodeTextView) {
        final TextStyleBuilder styleBuilder = new TextStyleBuilder();

        styleBuilder.withTextColor(colorCodeTextView);
        if (textTypeface != null) {
            styleBuilder.withTextFont(textTypeface);
        }

        addText(text, styleBuilder);
    }

    /**
     * This will update text and color on provided view
     *
     * @param view      view on which you want update
     * @param inputText text to update {@link TextView}
     * @param colorCode color to update on {@link TextView}
     */
    public void editText(@NonNull View view, String inputText, @NonNull int colorCode) {
        editText(view, null, inputText, colorCode);
    }

    /**
     * This will update the text and color on provided view
     *
     * @param view         root view where text view is a child
     * @param textTypeface update typeface for custom font in the text
     * @param inputText    text to update {@link TextView}
     * @param colorCode    color to update on {@link TextView}
     */
    public void editText(@NonNull View view, @Nullable Typeface textTypeface, String inputText, @NonNull int colorCode) {
        final TextStyleBuilder styleBuilder = new TextStyleBuilder();
        styleBuilder.withTextColor(colorCode);
        if (textTypeface != null) {
            styleBuilder.withTextFont(textTypeface);
        }

        editText(view, inputText, styleBuilder);
    }

    /**
     * This will update the text and color on provided view
     *
     * @param view         root view where text view is a child
     * @param inputText    text to update {@link TextView}
     * @param styleBuilder style to apply on {@link TextView}
     */
    public void editText(@NonNull View view, String inputText, @Nullable TextStyleBuilder styleBuilder) {
        TextView inputTextView = view.findViewById(R.id.tvPhotoEditorText);
        if (inputTextView != null && addedViews.contains(view) && !TextUtils.isEmpty(inputText)) {
            inputTextView.setText(inputText);
            if (styleBuilder != null)
                styleBuilder.applyStyle(inputTextView);

            parentView.updateViewLayout(view, view.getLayoutParams());
            int i = addedViews.indexOf(view);
            if (i > -1) addedViews.set(i, view);
        }
    }

    public void editText(@NonNull View view, @Nullable Typeface textTypeface, String inputText, int shadowColor, int gravity, float sizes, int colorCodeTextView, int paintFlags) {
        final TextStyleBuilder styleBuilder = new TextStyleBuilder();
        styleBuilder.withTextColor(colorCodeTextView);
        if (textTypeface != null) {
            styleBuilder.withTextFont(textTypeface);
        }

        editText(view, inputText, shadowColor, gravity, sizes, styleBuilder, paintFlags);
    }

    private void editText(View view, String inputText, int shadowColor, int gravity, float sizes, @Nullable TextStyleBuilder styleBuilder, int paintFlags) {
        TextView inputTextView = view.findViewById(R.id.tvPhotoEditorText);
        FrameLayout frmBorder = view.findViewById(R.id.frmBorder);
        if (inputTextView != null && addedViews.contains(view) && !TextUtils.isEmpty(inputText)) {
            inputTextView.setText(inputText);
            if (styleBuilder != null) styleBuilder.applyStyle(inputTextView);
            inputTextView.setPaintFlags(paintFlags);
            inputTextView.setShadowLayer(2f, 1.5f, 1.5f, shadowColor);
            inputTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizes);
            if (gravity == Gravity.START) {
                frmBorder.setBackgroundResource(R.drawable.rounded_left_border_tv);
            } else if (gravity == Gravity.END) {
                frmBorder.setBackgroundResource(R.drawable.rounded_right_border_tv);
            } else {
                frmBorder.setBackgroundResource(R.drawable.rounded_border_tv);
            }
            parentView.updateViewLayout(view, view.getLayoutParams());
            int i = addedViews.indexOf(view);
            if (i > -1) addedViews.set(i, view);
        }
    }

    /**
     * Adds emoji to the {@link PhotoEditorView} which you drag,rotate and scale using pinch
     * if {@link PhotoEditor.Builder#setPinchTextScalable(boolean)} enabled
     *
     * @param emojiName unicode in form of string to display emoji
     */
    public void addEmoji(String emojiName) {
        addEmoji(null, emojiName);
    }

    /**
     * Adds emoji to the {@link PhotoEditorView} which you drag,rotate and scale using pinch
     * if {@link PhotoEditor.Builder#setPinchTextScalable(boolean)} enabled
     *
     * @param emojiTypeface typeface for custom font to show emoji unicode in specific font
     * @param emojiName     unicode in form of string to display emoji
     */
    public void addEmoji(Typeface emojiTypeface, String emojiName) {
        brushDrawingView.setBrushDrawingMode(false);
        final View emojiRootView = getLayout(ViewType.EMOJI);
        final TextView emojiTextView = emojiRootView.findViewById(R.id.tvPhotoEditorText);
        final FrameLayout frmBorder = emojiRootView.findViewById(R.id.frmBorder);
        final ImageView imgClose = emojiRootView.findViewById(R.id.imgPhotoEditorClose);
        final ImageView imgEdit = emojiRootView.findViewById(R.id.imgPhotoEditorEdit);

        if (emojiTypeface != null) {
            emojiTextView.setTypeface(emojiTypeface);
        }
        emojiTextView.setTextSize(56);
        emojiTextView.setText(emojiName);
        MultiTouchListener multiTouchListener = getMultiTouchListener();
        multiTouchListener.setOnGestureControl(new MultiTouchListener.OnGestureControl() {
            @Override
            public void onClick() {
                boolean isBackgroundVisible = frmBorder.getTag() != null && (boolean) frmBorder.getTag();
                frmBorder.setBackgroundResource(isBackgroundVisible ? 0 : R.drawable.rounded_border_tv);
                imgClose.setVisibility(isBackgroundVisible ? View.INVISIBLE : View.VISIBLE);
                imgEdit.setVisibility(isBackgroundVisible ? View.INVISIBLE : View.VISIBLE);
                frmBorder.setTag(!isBackgroundVisible);
            }

            @Override
            public void onLongClick() {
            }

            @Override
            public void onTransform(float x, float y, float scaleX, float scaleY, float rotation, int width, int height) {

            }
        });
        emojiRootView.setOnTouchListener(multiTouchListener);
        addViewToParent(emojiRootView, ViewType.EMOJI);
    }

    public void handlerGoneFrame(final View rootView, final ViewType viewTypes, final String url, final int gravity, boolean isShow) {
        final FrameLayout frmBorder = rootView.findViewById(R.id.frmBorder);
        final ImageView imgClose = rootView.findViewById(R.id.imgPhotoEditorClose);
        final ImageView imgEdit = rootView.findViewById(R.id.imgPhotoEditorEdit);
        final ImageView imgReset = rootView.findViewById(R.id.imgPhotoEditorReset);

        if (isShow) {
            if (viewTypes == ViewType.IMAGE) {
                final ImageView imageView = rootView.findViewById(R.id.imgPhotoEditorImage);
                frmBorder.setBackgroundResource(R.drawable.rounded_border_tv);
                imgClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnPhotoEditorListener != null) {
                            mOnPhotoEditorListener.onRemoveViewListener(ViewType.IMAGE, rootView, addedViews.size());
                        }
                        try {
                            addedViews.remove(rootView);
                            parentView.removeView(rootView);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                imgEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnPhotoEditorListener != null) {
                            mOnPhotoEditorListener.onEditImageChangeListener(rootView, imageView, url);
                        }
                    }
                });

                imgReset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        rootView.setRotation(0.0f);
                    }
                });
            } else {
                final TextView textInputTv = rootView.findViewById(R.id.tvPhotoEditorText);
                if (gravity == Gravity.START) {
                    frmBorder.setBackgroundResource(R.drawable.rounded_left_border_tv);
                } else if (gravity == Gravity.END) {
                    frmBorder.setBackgroundResource(R.drawable.rounded_right_border_tv);
                } else {
                    frmBorder.setBackgroundResource(R.drawable.rounded_border_tv);
                }
                imgClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnPhotoEditorListener != null) {
                            mOnPhotoEditorListener.onRemoveViewListener(ViewType.TEXT, textInputTv, addedViews.size());
                        }
                        try {
                            addedViews.remove(rootView);
                            parentView.removeView(rootView);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                imgEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String textInput = textInputTv.getText().toString();
                        int currentTextColor = textInputTv.getCurrentTextColor();
                        if (mOnPhotoEditorListener != null) {
                            mOnPhotoEditorListener.onEditTextChangeListener(rootView, textInputTv, textInput, currentTextColor);
                        }
                    }
                });

                imgReset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        rootView.setRotation(0.0f);
                    }
                });
            }
            if (frmBorder.getVisibility() == View.INVISIBLE) frmBorder.setVisibility(View.VISIBLE);
//            rootView.setEnabled(true);
            imgEdit.setVisibility(View.VISIBLE);
            imgReset.setVisibility(View.VISIBLE);
            imgClose.setVisibility(View.VISIBLE);
            frmBorder.setTag(true);
        } else {
            frmBorder.setBackgroundResource(0);
            imgEdit.setVisibility(View.INVISIBLE);
            imgReset.setVisibility(View.INVISIBLE);
            imgClose.setVisibility(View.INVISIBLE);
            frmBorder.setTag(false);
//            imgClose.setOnClickListener(null);
//            imgEdit.setOnClickListener(null);
//            imgReset.setOnClickListener(null);
        }
    }

    public void handlerGoneFrame(View rootView) {
        final FrameLayout frmBorder = rootView.findViewById(R.id.frmBorder);
        final ImageView imgClose = rootView.findViewById(R.id.imgPhotoEditorClose);
        final ImageView imgEdit = rootView.findViewById(R.id.imgPhotoEditorEdit);
        final ImageView imgReset = rootView.findViewById(R.id.imgPhotoEditorReset);

        frmBorder.setBackgroundResource(0);
        imgEdit.setVisibility(View.INVISIBLE);
        imgClose.setVisibility(View.INVISIBLE);
        imgReset.setVisibility(View.INVISIBLE);
        frmBorder.setTag(false);
        frmBorder.setEnabled(false);
//        rootView.setEnabled(false);
//        imgClose.setOnClickListener(null);
//        imgEdit.setOnClickListener(null);
//        imgReset.setOnClickListener(null);
    }

    public void handlerShowGone(View textRootView, boolean isShow) {
        final FrameLayout frmBorder = textRootView.findViewById(R.id.frmBorder);
        final ImageView imgClose = textRootView.findViewById(R.id.imgPhotoEditorClose);
        final ImageView imgEdit = textRootView.findViewById(R.id.imgPhotoEditorEdit);
        final ImageView imgReset = textRootView.findViewById(R.id.imgPhotoEditorReset);
        frmBorder.setBackgroundResource(0);
        frmBorder.setTag(isShow);
        imgEdit.setVisibility(View.INVISIBLE);
        imgReset.setVisibility(View.INVISIBLE);
        imgClose.setVisibility(View.INVISIBLE);
        frmBorder.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
        textRootView.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
        textRootView.setEnabled(false);
        imgClose.setOnClickListener(null);
        imgEdit.setOnClickListener(null);
        imgReset.setOnClickListener(null);
    }

    public void handlerGoneAll(View textRootView, boolean isShow) {
        final FrameLayout frmBorder = textRootView.findViewById(R.id.frmBorder);
        final ImageView imgClose = textRootView.findViewById(R.id.imgPhotoEditorClose);
        final ImageView imgEdit = textRootView.findViewById(R.id.imgPhotoEditorEdit);
        final ImageView imgReset = textRootView.findViewById(R.id.imgPhotoEditorReset);
        frmBorder.setBackgroundResource(0);
        frmBorder.setTag(isShow);
        imgEdit.setVisibility(View.INVISIBLE);
        imgReset.setVisibility(View.INVISIBLE);
        imgClose.setVisibility(View.INVISIBLE);
        frmBorder.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
//        textRootView.setEnabled(false);
//        imgClose.setOnClickListener(null);
//        imgEdit.setOnClickListener(null);
//        imgReset.setOnClickListener(null);
    }

    public void handlerShowAll(final View rootView, final String url, boolean isSticker) {
        final FrameLayout frmBorder = rootView.findViewById(R.id.frmBorder);
        final ImageView imgClose = rootView.findViewById(R.id.imgPhotoEditorClose);
        final ImageView imgEdit = rootView.findViewById(R.id.imgPhotoEditorEdit);
        final ImageView imgReset = rootView.findViewById(R.id.imgPhotoEditorReset);
        frmBorder.setTag(true);
        imgEdit.setVisibility(View.INVISIBLE);
        imgReset.setVisibility(View.INVISIBLE);
        imgClose.setVisibility(View.INVISIBLE);
        frmBorder.setVisibility(View.VISIBLE);
        rootView.setEnabled(true);
        if (isSticker) {
            final ImageView imageView = rootView.findViewById(R.id.imgPhotoEditorImage);
            imgClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnPhotoEditorListener != null) {
                        mOnPhotoEditorListener.onRemoveViewListener(ViewType.IMAGE, rootView, addedViews.size());
                    }
                    try {
                        addedViews.remove(rootView);
                        parentView.removeView(rootView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            imgEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnPhotoEditorListener != null) {
                        mOnPhotoEditorListener.onEditImageChangeListener(rootView, imageView, url);
                    }
                }
            });

            imgReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rootView.setRotation(0.0f);
                }
            });
        } else {
            final TextView textInputTv = rootView.findViewById(R.id.tvPhotoEditorText);
            imgClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnPhotoEditorListener != null) {
                        mOnPhotoEditorListener.onRemoveViewListener(ViewType.TEXT, textInputTv, addedViews.size());
                    }
                    try {
                        addedViews.remove(rootView);
                        parentView.removeView(rootView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            imgEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String textInput = textInputTv.getText().toString();
                    int currentTextColor = textInputTv.getCurrentTextColor();
                    if (mOnPhotoEditorListener != null) {
                        mOnPhotoEditorListener.onEditTextChangeListener(rootView, textInputTv, textInput, currentTextColor);
                    }
                }
            });

            imgReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rootView.setRotation(0.0f);
                }
            });
        }
    }

    public int checkGone(View textRootView) {
        FrameLayout frmBorder = textRootView.findViewById(R.id.frmBorder);
        return frmBorder.getVisibility();
    }

    /**
     * Add to root view from image,emoji and text to our parent view
     *
     * @param rootView rootview of image,text and emoji
     */
    private void addViewToParent(View rootView, ViewType viewType) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        parentView.addView(rootView, params);
        addedViews.add(rootView);
        if (mOnPhotoEditorListener != null)
            mOnPhotoEditorListener.onAddViewListener(viewType, addedViews.size());
    }

    /**
     * Create a new instance and scalable touchview
     *
     * @return scalable multitouch listener
     */
    @NonNull
    private MultiTouchListener getMultiTouchListener() {
        MultiTouchListener multiTouchListener = new MultiTouchListener(
                deleteView,
                parentView,
                this.imageView,
                isTextPinchZoomable,
                mOnPhotoEditorListener);

        //multiTouchListener.setOnMultiTouchListener(this);

        return multiTouchListener;
    }

    /**
     * Get root view by its type i.e image,text and emoji
     *
     * @param viewType image,text or emoji
     * @return rootview
     */
    private View getLayout(final ViewType viewType) {
        View rootView = null;
        switch (viewType) {
            case TEXT:
                rootView = mLayoutInflater.inflate(R.layout.view_photo_editor_text, null);
                TextView txtText = rootView.findViewById(R.id.tvPhotoEditorText);
                if (txtText != null && mDefaultTextTypeface != null) {
                    txtText.setGravity(Gravity.CENTER);
                    if (mDefaultEmojiTypeface != null) {
                        txtText.setTypeface(mDefaultTextTypeface);
                    }
                }
                break;
            case IMAGE:
                rootView = mLayoutInflater.inflate(R.layout.view_photo_editor_image, null);
                break;
            case EMOJI:
                rootView = mLayoutInflater.inflate(R.layout.view_photo_editor_text, null);
                TextView txtTextEmoji = rootView.findViewById(R.id.tvPhotoEditorText);
                if (txtTextEmoji != null) {
                    if (mDefaultEmojiTypeface != null) {
                        txtTextEmoji.setTypeface(mDefaultEmojiTypeface);
                    }
                    txtTextEmoji.setGravity(Gravity.CENTER);
                    txtTextEmoji.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                }
                break;
        }

        if (rootView != null) {
            //We are setting tag as ViewType to identify what type of the view it is
            //when we remove the view from stack i.e onRemoveViewListener(ViewType viewType, int numberOfAddedViews);
            rootView.setTag(viewType);
            final ImageView imgClose = rootView.findViewById(R.id.imgPhotoEditorClose);
            final View finalRootView = rootView;
            if (imgClose != null) {
                imgClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewUndo(finalRootView, viewType);
                    }
                });
            }
        }
        return rootView;
    }

    /**
     * Enable/Disable drawing mode to draw on {@link PhotoEditorView}
     *
     * @param brushDrawingMode true if mode is enabled
     */
    public void setBrushDrawingMode(boolean brushDrawingMode) {
        if (brushDrawingView != null)
            brushDrawingView.setBrushDrawingMode(brushDrawingMode);
    }

    /**
     * @return true is brush mode is enabled
     */
    public Boolean getBrushDrawableMode() {
        return brushDrawingView != null && brushDrawingView.getBrushDrawingMode();
    }

    /**
     * set the size of bursh user want to paint on canvas i.e {@link BrushDrawingView}
     *
     * @param size size of brush
     */
    public void setBrushSize(float size) {
        if (brushDrawingView != null)
            brushDrawingView.setBrushSize(size);
    }

    /**
     * set opacity/transparency of brush while painting on {@link BrushDrawingView}
     *
     * @param opacity opacity is in form of percentage
     */
    public void setOpacity(@IntRange(from = 0, to = 100) int opacity) {
        if (brushDrawingView != null) {
            opacity = (int) ((opacity / 100.0) * 255.0);
            brushDrawingView.setOpacity(opacity);
        }
    }

    /**
     * set brush color which user want to paint
     *
     * @param color color value for paint
     */
    public void setBrushColor(@ColorInt int color) {
        if (brushDrawingView != null)
            brushDrawingView.setBrushColor(color);
    }

    /**
     * set the eraser size
     * <br></br>
     * <b>Note :</b> Eraser size is different from the normal brush size
     *
     * @param brushEraserSize size of eraser
     */
    public void setBrushEraserSize(float brushEraserSize) {
        if (brushDrawingView != null)
            brushDrawingView.setBrushEraserSize(brushEraserSize);
    }

    void setBrushEraserColor(@ColorInt int color) {
        if (brushDrawingView != null)
            brushDrawingView.setBrushEraserColor(color);
    }

    /**
     * @return provide the size of eraser
     * @see PhotoEditor#setBrushEraserSize(float)
     */
    public float getEraserSize() {
        return brushDrawingView != null ? brushDrawingView.getEraserSize() : 0;
    }

    /**
     * @return provide the size of eraser
     * @see PhotoEditor#setBrushSize(float)
     */
    public float getBrushSize() {
        if (brushDrawingView != null)
            return brushDrawingView.getBrushSize();
        return 0;
    }

    /**
     * @return provide the size of eraser
     * @see PhotoEditor#setBrushColor(int)
     */
    public int getBrushColor() {
        if (brushDrawingView != null)
            return brushDrawingView.getBrushColor();
        return 0;
    }

    /**
     * <p>
     * Its enables eraser mode after that whenever user drags on screen this will erase the existing
     * paint
     * <br>
     * <b>Note</b> : This eraser will work on paint views only
     * <p>
     */
    public void brushEraser() {
        if (brushDrawingView != null)
            brushDrawingView.brushEraser();
    }

    /*private void viewUndo() {
        if (addedViews.size() > 0) {
            parentView.removeView(addedViews.remove(addedViews.size() - 1));
            if (mOnPhotoEditorListener != null)
                mOnPhotoEditorListener.onRemoveViewListener(addedViews.size());
        }
    }*/

    private void viewUndo(View removedView, ViewType viewType) {
        if (addedViews.size() > 0) {
            if (addedViews.contains(removedView)) {
                parentView.removeView(removedView);
                addedViews.remove(removedView);
                redoViews.add(removedView);
                if (mOnPhotoEditorListener != null) {
                    mOnPhotoEditorListener.onRemoveViewListener(viewType, removedView, addedViews.size());
                }
            }
        }
    }

    /**
     * Undo the last operation perform on the {@link PhotoEditor}
     *
     * @return true if there nothing more to undo
     */
    public boolean undo() {
        if (addedViews.size() > 0) {
            View removeView = addedViews.get(addedViews.size() - 1);
            if (removeView instanceof BrushDrawingView) {
                return brushDrawingView != null && brushDrawingView.undo();
            } else {
                addedViews.remove(addedViews.size() - 1);
                parentView.removeView(removeView);
                redoViews.add(removeView);
            }
            if (mOnPhotoEditorListener != null) {
                Object viewTag = removeView.getTag();
                if (viewTag != null && viewTag instanceof ViewType) {
                    mOnPhotoEditorListener.onRemoveViewListener(((ViewType) viewTag), removeView, addedViews.size());
                }
            }
        }
        return addedViews.size() != 0;
    }

    /**
     * Redo the last operation perform on the {@link PhotoEditor}
     *
     * @return true if there nothing more to redo
     */
    public boolean redo() {
        if (redoViews.size() > 0) {
            View redoView = redoViews.get(redoViews.size() - 1);
            if (redoView instanceof BrushDrawingView) {
                return brushDrawingView != null && brushDrawingView.redo();
            } else {
                redoViews.remove(redoViews.size() - 1);
                parentView.addView(redoView);
                addedViews.add(redoView);
            }
            Object viewTag = redoView.getTag();
            if (mOnPhotoEditorListener != null && viewTag != null && viewTag instanceof ViewType) {
                mOnPhotoEditorListener.onAddViewListener(((ViewType) viewTag), addedViews.size());
            }
        }
        return redoViews.size() != 0;
    }

    private void clearBrushAllViews() {
        if (brushDrawingView != null)
            brushDrawingView.clearAll();
    }

    /**
     * Removes all the edited operations performed {@link PhotoEditorView}
     * This will also clear the undo and redo stack
     */
    public void clearAllViews() {
        for (int i = 0; i < addedViews.size(); i++) {
            parentView.removeView(addedViews.get(i));
        }
        if (addedViews.contains(brushDrawingView)) {
            parentView.addView(brushDrawingView);
        }
        addedViews.clear();
        redoViews.clear();
        clearBrushAllViews();
    }

    /**
     * Remove all helper boxes from views
     */
    @UiThread
    public void clearHelperBox() {
        for (int i = 0; i < parentView.getChildCount(); i++) {
            View childAt = parentView.getChildAt(i);
            FrameLayout frmBorder = childAt.findViewById(R.id.frmBorder);
            if (frmBorder != null) {
                frmBorder.setBackgroundResource(0);
            }
            ImageView imgClose = childAt.findViewById(R.id.imgPhotoEditorClose);
            ImageView imgEdit = childAt.findViewById(R.id.imgPhotoEditorEdit);
            ImageView imgReset = childAt.findViewById(R.id.imgPhotoEditorReset);
            if (imgClose != null) {
                imgClose.setVisibility(View.INVISIBLE);
                imgEdit.setVisibility(View.INVISIBLE);
                imgReset.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * Setup of custom effect using effect type and set parameters values
     *
     * @param customEffect {@link CustomEffect.Builder#setParameter(String, Object)}
     */
    public void setFilterEffect(CustomEffect customEffect) {
        parentView.setFilterEffect(customEffect);
    }

    /**
     * Set pre-define filter available
     *
     * @param filterType type of filter want to apply {@link PhotoEditor}
     */
    public void setFilterEffect(PhotoFilter filterType) {
        parentView.setFilterEffect(filterType);
    }

    /**
     * A callback to save the edited image asynchronously
     */
    public interface OnSaveListener {

        /**
         * Call when edited image is saved successfully on given path
         *
         * @param imagePath path on which image is saved
         */
        void onSuccess(@NonNull String imagePath);

        /**
         * Call when failed to saved image on given path
         *
         * @param exception exception thrown while saving image
         */
        void onFailure(@NonNull Exception exception);
    }

    public interface OnSaveBitmapListener {
        void onSuccess(@NonNull Bitmap bitmap);

        void onFailure(@NonNull Exception exception);
    }


    /**
     * Save the edited image on given path
     *
     * @param imagePath      path on which image to be saved
     * @param onSaveListener callback for saving image
     * @see OnSaveListener
     */
    @RequiresPermission(allOf = {Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void saveAsFile(@NonNull final String imagePath,
                           @NonNull final OnSaveListener onSaveListener) {
        saveAsFile(imagePath, new SaveSettings.Builder().build(), onSaveListener);
    }

    /**
     * Save the edited image on given path
     *
     * @param imagePath      path on which image to be saved
     * @param saveSettings   builder for multiple save options {@link SaveSettings}
     * @param onSaveListener callback for saving image
     * @see OnSaveListener
     */
    @SuppressLint("StaticFieldLeak")
    @RequiresPermission(allOf = {Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void saveAsFile(@NonNull final String imagePath,
                           @NonNull final SaveSettings saveSettings,
                           @NonNull final OnSaveListener onSaveListener) {
        Log.d(TAG, "Image Path: " + imagePath);
        parentView.saveFilter(new OnSaveBitmap() {
            @Override
            public void onBitmapReady(Bitmap saveBitmap) {
                new AsyncTask<String, String, Exception>() {

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        clearHelperBox();
                        parentView.setDrawingCacheEnabled(false);
                    }

                    @SuppressLint("MissingPermission")
                    @Override
                    protected Exception doInBackground(String... strings) {
                        // Create a media file name
                        File file = new File(imagePath);
                        try {
                            FileOutputStream out = new FileOutputStream(file, false);
                            if (parentView != null) {
                                parentView.setDrawingCacheEnabled(true);
                                Bitmap drawingCache = saveSettings.isTransparencyEnabled()
                                        ? BitmapUtil.removeTransparency(parentView.getDrawingCache())
                                        : parentView.getDrawingCache();
                                drawingCache.compress(saveSettings.getCompressFormat(), saveSettings.getCompressQuality(), out);
                            }
                            out.flush();
                            out.close();
                            Log.d(TAG, "Filed Saved Successfully");
                            return null;
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d(TAG, "Failed to save File");
                            return e;
                        }
                    }

                    @Override
                    protected void onPostExecute(Exception e) {
                        super.onPostExecute(e);
                        if (e == null) {
                            //Clear all views if its enabled in save settings
                            if (saveSettings.isClearViewsEnabled()) clearAllViews();
                            onSaveListener.onSuccess(imagePath);
                        } else {
                            onSaveListener.onFailure(e);
                        }
                    }

                }.execute();
            }

            @Override
            public void onFailure(Exception e) {
                onSaveListener.onFailure(e);
            }
        });
    }

    public void saveAsBitmap(@NonNull final OnSaveBitmapListener onSaveListener) {
        parentView.saveFilter(new OnSaveBitmap() {
            @Override
            public void onBitmapReady(Bitmap saveBitmap) {
                clearHelperBox();
                parentView.setDrawingCacheEnabled(true);
                onSaveListener.onSuccess(parentView.getDrawingCache());
            }

            @Override
            public void onFailure(Exception e) {
                onSaveListener.onFailure(e);
            }
        });
    }

    /**
     * Save the edited image as bitmap
     *
     * @param onSaveBitmap callback for saving image as bitmap
     * @see OnSaveBitmap
     */
    @SuppressLint("StaticFieldLeak")
    public void saveAsBitmap(@NonNull final OnSaveBitmap onSaveBitmap) {
        saveAsBitmap(new SaveSettings.Builder().build(), onSaveBitmap);
    }

    /**
     * Save the edited image as bitmap
     *
     * @param saveSettings builder for multiple save options {@link SaveSettings}
     * @param onSaveBitmap callback for saving image as bitmap
     * @see OnSaveBitmap
     */
    @SuppressLint("StaticFieldLeak")
    public void saveAsBitmap(@NonNull final SaveSettings saveSettings,
                             @NonNull final OnSaveBitmap onSaveBitmap) {
        parentView.saveFilter(new OnSaveBitmap() {
            @Override
            public void onBitmapReady(Bitmap saveBitmap) {
                new AsyncTask<String, String, Bitmap>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        clearHelperBox();
                        parentView.setDrawingCacheEnabled(false);
                    }

                    @Override
                    protected Bitmap doInBackground(String... strings) {
                        if (parentView != null) {
                            parentView.setDrawingCacheEnabled(true);
                            return saveSettings.isTransparencyEnabled() ?
                                    BitmapUtil.removeTransparency(parentView.getDrawingCache())
                                    : parentView.getDrawingCache();
                        } else {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        super.onPostExecute(bitmap);
                        if (bitmap != null) {
                            if (saveSettings.isClearViewsEnabled()) clearAllViews();
                            onSaveBitmap.onBitmapReady(bitmap);
                        } else {
                            onSaveBitmap.onFailure(new Exception("Failed to load the bitmap"));
                        }
                    }

                }.execute();
            }

            @Override
            public void onFailure(Exception e) {
                onSaveBitmap.onFailure(e);
            }
        });
    }

    private static String convertEmoji(String emoji) {
        String returnedEmoji;
        try {
            int convertEmojiToInt = Integer.parseInt(emoji.substring(2), 16);
            returnedEmoji = new String(Character.toChars(convertEmojiToInt));
        } catch (NumberFormatException e) {
            returnedEmoji = "";
        }
        return returnedEmoji;
    }

    /**
     * Callback on editing operation perform on {@link PhotoEditorView}
     *
     * @param onPhotoEditorListener {@link OnPhotoEditorListener}
     */
    public void setOnPhotoEditorListener(@NonNull OnPhotoEditorListener onPhotoEditorListener) {
        this.mOnPhotoEditorListener = onPhotoEditorListener;
    }

    /**
     * Check if any changes made need to save
     *
     * @return true if nothing is there to change
     */
    public boolean isCacheEmpty() {
        return addedViews.size() == 0 && redoViews.size() == 0;
    }


    @Override
    public void onViewAdd(BrushDrawingView brushDrawingView) {
        if (redoViews.size() > 0) {
            redoViews.remove(redoViews.size() - 1);
        }
        addedViews.add(brushDrawingView);
        if (mOnPhotoEditorListener != null) {
            mOnPhotoEditorListener.onAddViewListener(ViewType.BRUSH_DRAWING, addedViews.size());
        }
    }

    @Override
    public void onViewRemoved(BrushDrawingView brushDrawingView) {
        if (addedViews.size() > 0) {
            View removeView = addedViews.remove(addedViews.size() - 1);
            if (!(removeView instanceof BrushDrawingView)) {
                parentView.removeView(removeView);
            }
            redoViews.add(removeView);
        }
        if (mOnPhotoEditorListener != null) {
            mOnPhotoEditorListener.onRemoveViewListener(ViewType.BRUSH_DRAWING, brushDrawingView, addedViews.size());
        }
    }

    @Override
    public void onStartDrawing() {
        if (mOnPhotoEditorListener != null) {
            mOnPhotoEditorListener.onStartViewChangeListener(ViewType.BRUSH_DRAWING);
        }
    }

    @Override
    public void onStopDrawing() {
        if (mOnPhotoEditorListener != null) {
            mOnPhotoEditorListener.onStopViewChangeListener(ViewType.BRUSH_DRAWING);
        }
    }


    /**
     * Builder pattern to define {@link PhotoEditor} Instance
     */
    public static class Builder {

        private Context context;
        private PhotoEditorView parentView;
        private ImageView imageView;
        private View deleteView;
        private BrushDrawingView brushDrawingView;
        private Typeface textTypeface;
        private Typeface emojiTypeface;
        //By Default pinch zoom on text is enabled
        private boolean isTextPinchZoomable = true;

        /**
         * Building a PhotoEditor which requires a Context and PhotoEditorView
         * which we have setup in our xml layout
         *
         * @param context         context
         * @param photoEditorView {@link PhotoEditorView}
         */
        public Builder(Context context, PhotoEditorView photoEditorView) {
            this.context = context;
            parentView = photoEditorView;
            imageView = photoEditorView.getSource();
            brushDrawingView = photoEditorView.getBrushDrawingView();
        }

        Builder setDeleteView(View deleteView) {
            this.deleteView = deleteView;
            return this;
        }

        /**
         * set default text font to be added on image
         *
         * @param textTypeface typeface for custom font
         * @return {@link Builder} instant to build {@link PhotoEditor}
         */
        public Builder setDefaultTextTypeface(Typeface textTypeface) {
            this.textTypeface = textTypeface;
            return this;
        }

        /**
         * set default font specific to add emojis
         *
         * @param emojiTypeface typeface for custom font
         * @return {@link Builder} instant to build {@link PhotoEditor}
         */
        public Builder setDefaultEmojiTypeface(Typeface emojiTypeface) {
            this.emojiTypeface = emojiTypeface;
            return this;
        }

        /**
         * set false to disable pinch to zoom on text insertion.By deafult its true
         *
         * @param isTextPinchZoomable flag to make pinch to zoom
         * @return {@link Builder} instant to build {@link PhotoEditor}
         */
        public Builder setPinchTextScalable(boolean isTextPinchZoomable) {
            this.isTextPinchZoomable = isTextPinchZoomable;
            return this;
        }

        /**
         * @return build PhotoEditor instance
         */
        public PhotoEditor build() {
            return new PhotoEditor(this);
        }
    }

    /**
     * Provide the list of emoji in form of unicode string
     *
     * @param context context
     * @return list of emoji unicode
     */
    public static ArrayList<String> getEmojis(Context context) {
        ArrayList<String> convertedEmojiList = new ArrayList<>();
        String[] emojiList = context.getResources().getStringArray(R.array.photo_editor_emoji);
        for (String emojiUnicode : emojiList) {
            convertedEmojiList.add(convertEmoji(emojiUnicode));
        }
        return convertedEmojiList;
    }

    public interface OnGetViewRoot {
        void onGetView(View view);

        void onMoveEditorEventListener(View view, ViewType viewType);
    }

    public interface OnEditorCallBack {
        void onEditView(View view);
    }
}
