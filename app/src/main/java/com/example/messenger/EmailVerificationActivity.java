<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="24dp"
    android:gravity="center"
    android:background="@color/white">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Messenger"
        android:textSize="28sp"
        android:textStyle="bold"
        android:textColor="@color/telegram_blue"
        android:layout_marginBottom="48dp" />

    <com.google.android.material.textfield.TextInputLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Email"
        style="@style/Widget.MaterialComponents.TextInputLayout.OutlinedBox"
        app:boxStrokeColor="@color/telegram_blue"
        app:hintTextColor="@color/telegram_blue"
        app:boxStrokeWidth="1dp"
        app:boxStrokeWidthFocused="2dp"
        app:placeholderText="example@mail.com"
        app:placeholderTextColor="@color/gray_text">

        <com.google.android.material.textfield.TextInputEditText
            android:id="@+id/etEmail"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:inputType="textEmailAddress"
            android:textColor="@color/black"
            android:textColorHint="@color/gray_text" />
    </com.google.android.material.textfield.TextInputLayout>

    <Button
        android:id="@+id/btnSendCode"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Отправить код"
        android:backgroundTint="@color/telegram_blue"
        android:layout_marginTop="16dp" />

    <com.google.android.material.textfield.TextInputLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Код из письма"
        style="@style/Widget.MaterialComponents.TextInputLayout.OutlinedBox"
        app:boxStrokeColor="@color/telegram_blue"
        app:hintTextColor="@color/telegram_blue"
        app:boxStrokeWidth="1dp"
        app:boxStrokeWidthFocused="2dp"
        android:layout_marginTop="24dp"
        app:placeholderText="6 цифр"
        app:placeholderTextColor="@color/gray_text">

        <com.google.android.material.textfield.TextInputEditText
            android:id="@+id/etCode"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:inputType="number"
            android:textColor="@color/black"
            android:textColorHint="@color/gray_text" />
    </com.google.android.material.textfield.TextInputLayout>

    <Button
        android:id="@+id/btnVerify"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Подтвердить"
        android:backgroundTint="@color/telegram_blue"
        android:enabled="false"
        android:layout_marginTop="16dp" />

    <TextView
        android:id="@+id/tvStatus"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        android:textSize="12sp"
        android:textColor="@color/gray_text" />
</LinearLayout>
