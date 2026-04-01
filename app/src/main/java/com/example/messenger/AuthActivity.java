@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_auth);
    
    // Временный переход для проверки
    startActivity(new Intent(this, MainActivity.class));
    finish();
}
