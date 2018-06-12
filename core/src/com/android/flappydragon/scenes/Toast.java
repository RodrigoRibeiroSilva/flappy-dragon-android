package com.android.flappydragon.scenes;

/**
 * Created by Rodrigo on 10/06/2018.
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Align;

public class Toast {
    public enum Length {
        SHORT(20f),
        LONG(3.5f);

        private final float duration; // in seconds

        Length(float duration) {
            this.duration = duration;
        }
    }

    private String msg;
    private final BitmapFont font;
    private float fadingDuration = 0.5f;
    private final Color fontColor;
    private SpriteBatch spriteBatch = new SpriteBatch();
    private ShapeRenderer renderer = new ShapeRenderer();

    private float opacity = 1f;
    private int toastWidth;
    private int toastHeight;
    private float timeToLive;
    private float positionX, positionY; // left bottom corner
    private float fontX, fontY; // left top corner
    private int fontWidth;

    Toast(
            String text,
            Length length,
            BitmapFont font,
            Color backgroundColor,
            float fadingDuration,
            float maxRelativeWidth,
            Color fontColor,
            float positionY,
            Integer customMargin
    ) {
        this.msg = text;
        this.font = font;
        this.fadingDuration = fadingDuration;
        this.positionY = positionY;
        this.fontColor = fontColor;

        this.timeToLive = length.duration;
        renderer.setColor(backgroundColor);

        // measure text box
        GlyphLayout layoutSimple = new GlyphLayout();
        layoutSimple.setText(this.font, text);

        int lineHeight = (int) layoutSimple.height;
        fontWidth = (int) (layoutSimple.width);
        int fontHeight = (int) (layoutSimple.height);

        int margin = customMargin == null ? lineHeight * 2 : customMargin;

        float screenWidth = Gdx.graphics.getWidth();
        float maxTextWidth = screenWidth * maxRelativeWidth;
        if (fontWidth > maxTextWidth) {
            BitmapFontCache cache = new BitmapFontCache(this.font, true);
            GlyphLayout layout = cache.addText(text, 0, 0, maxTextWidth, Align.center, true);
            fontWidth = (int) layout.width;
            fontHeight = (int) layout.height;
        }

        toastHeight = fontHeight + 2 * margin;
        toastWidth = fontWidth + 2 * margin;

        positionX = (screenWidth / 2) - toastWidth / 2;

        fontX = positionX + margin;
        fontY = positionY + margin + fontHeight;
    }

    public boolean render(float delta) {
        timeToLive -= delta / 80;

        if (timeToLive < 0) {
            return false;
        }

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.circle(positionX, positionY + toastHeight / 2, toastHeight / 2);
        renderer.rect(positionX, positionY, toastWidth, toastHeight);
        renderer.circle(positionX + toastWidth, positionY + toastHeight / 2, toastHeight / 2);
        renderer.end();

        spriteBatch.begin();

        if (timeToLive > 0 && opacity > 0.15) {
            if (timeToLive < fadingDuration) {
                opacity = timeToLive / fadingDuration;
            }

            font.setColor(fontColor.r, fontColor.g, fontColor.b, fontColor.a * opacity);
            font.draw(spriteBatch, msg, fontX, fontY, fontWidth, Align.center, true);
        }

        spriteBatch.end();

        return true;
    }

    public static class ToastFactory {

        private BitmapFont font;
        private Color backgroundColor = new Color(105, 105, 105, 0);
        private Color fontColor = new Color(1, 1, 1, 1);
        private float positionY;
        private float fadingDuration = 0.5f;
        private float maxRelativeWidth = 0.65f;
        private Integer customMargin;

        private ToastFactory() {
            float screenHeight = Gdx.graphics.getHeight();
            float bottomGap = 100;
            positionY = bottomGap + ((screenHeight - bottomGap) / 10);
        }

        public Toast create(String text, Length length) {
            return new Toast(
                    text,
                    length,
                    font,
                    backgroundColor,
                    fadingDuration,
                    maxRelativeWidth,
                    fontColor,
                    positionY,
                    customMargin);
        }


        public static class Builder {

            private boolean built = false;
            private ToastFactory factory = new ToastFactory();

            public Builder font(BitmapFont font) {
                check();
                factory.font = font;
                return this;
            }

            public Builder backgroundColor(Color color) {
                check();
                factory.backgroundColor = color;
                return this;
            }


            public Builder fontColor(Color color) {
                check();
                factory.fontColor = color;
                return this;
            }


            public Builder positionY(float positionY) {
                check();
                factory.positionY = positionY;
                return this;
            }

            public Builder fadingDuration(float fadingDuration) {
                check();
                if (fadingDuration < 0) {
                    throw new IllegalArgumentException("Duration must be non-negative number");
                }
                factory.fadingDuration = fadingDuration;
                return this;
            }

            public Builder maxTextRelativeWidth(float maxTextRelativeWidth) {
                check();
                factory.maxRelativeWidth = maxTextRelativeWidth;
                return this;
            }

            public Builder margin(int margin) {
                check();
                factory.customMargin = margin;
                return this;
            }

            public ToastFactory build() {
                check();
                if (factory.font == null) {
                    throw new IllegalStateException("Font is not set");
                }
                built = true;
                return factory;
            }

            private void check() {
                if (built) {
                    throw new IllegalStateException("Builder can be used only once");
                }
            }
        }
    }

}