package com.example.mario.myapplication;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;

/**
 * Klasa LineButton
 *
 * to klasa reprezentująca przycisk wyboru linii.
 *
 * Metody klasy LineButton:
 *      void setAppearance() ->
 *          ustala wygląd przycisku.
 */
public class LineButton extends AppCompatTextView
{
    private boolean isActive = false;

    public LineButton(Context context)
    {
        super(context);
        setAppearance();

        final BusManagement.Selector selector = ((MainActivity)context).getBusManagement().getSelector();

        setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!isActive)
                {
                    setBackground(getContext().getDrawable(R.drawable.line_button_background_active));
                    setTextColor(getResources().getColor(R.color.colorWhite));
                    selector.setSelectedLines(getText().toString(), false);
                }
                else
                {
                    setBackground(getContext().getDrawable(R.drawable.line_button_background));
                    setTextColor(getResources().getColor(R.color.colorBlack));
                    selector.setSelectedLines(getText().toString(), true);
                }

                Log.i("Lista aktywnych:", selector.getSelectedLines().toString());

                isActive = !isActive;
            }
        });
    }

    public void setAppearance()
    {
        setPadding(20, 10, 20, 10);
        setTextSize(16);
        setBackground(getContext().getDrawable(R.drawable.line_button_background));
        setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
    }
}