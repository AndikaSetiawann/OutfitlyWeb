package com.example.outfitly;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class AiActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private EditText messageEditText;
    private FloatingActionButton sendButton;
    private LinearLayout typingIndicator;
    private List<Message> messageList = new ArrayList<>();
    private MessageAdapter messageAdapter;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private boolean isSystemInIndonesian;
    private boolean askedAboutDate = false;
    private boolean hasPartner = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            finish();
            return;
        }

        isSystemInIndonesian = Locale.getDefault().getLanguage().equals("id");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Tanya Asisten AI");
        }

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);
        typingIndicator = findViewById(R.id.typingIndicator);

        messageEditText.setHint("Tanyakan tentang rekomendasi outfit...");

        setupRecyclerView();
        setupExampleChips();
        setupClickListeners();

        addBotMessage(new OutfitRecommendation(
                "Halo! Tanyakan outfit untuk acara atau tempat tertentu ya, atau mungkin untuk jalan sama gebetan? 😊 ! 👗👕",
                null, null
        ));
    }

    private void setupRecyclerView() {
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageAdapter = new MessageAdapter(messageList, this);
        chatRecyclerView.setAdapter(messageAdapter);
    }

    private void setupExampleChips() {
        int[] exampleIds = {R.id.example1, R.id.example2, R.id.example3, R.id.example4};
        String[] examples = {"Outfit ke kampus", "Outfit wawancara kerja", "Outfit musim dingin", "Outfit ke pantai"};

        for (int i = 0; i < exampleIds.length; i++) {
            TextView example = findViewById(exampleIds[i]);
            example.setText(examples[i]);
            example.setOnClickListener(v -> sendQuickReply(((TextView) v).getText().toString()));
        }
    }

    private void setupClickListeners() {
        sendButton.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String messageText = messageEditText.getText().toString().trim();
        if (messageText.isEmpty()) return;

        addUserMessage(messageText);
        messageEditText.setText("");
        showTypingIndicator(true);
        simulateAiResponse(messageText);
    }

    private void simulateAiResponse(String userQuery) {
        int delay = 1200 + (int) (Math.random() * 800);
        new Handler().postDelayed(() -> {
            showTypingIndicator(false);

            // Handle date conversation flow
            if (askedAboutDate) {
                handleDateResponse(userQuery);
                return;
            }

            // Check if query is about dating
            if (containsDateKeywords(userQuery)) {
                askedAboutDate = true;
                addBotMessage(new OutfitRecommendation(
                        "Apakah kamu sudah punya pasangan? 😊",
                        null, null
                ));
                return;
            }

            OutfitRecommendation recommendation = generateResponse(userQuery);
            addBotMessage(recommendation);
        }, delay);
    }

    private boolean containsDateKeywords(String query) {
        query = query.toLowerCase(Locale.ROOT);
        List<String> dateKeywords = Arrays.asList("kencan", "first date", "jalan sama cewe", "jalan sama cowo",
                "jalan bareng pacar", "jalan sama gebetan", "ngedate");

        for (String keyword : dateKeywords) {
            if (query.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private void handleDateResponse(String response) {
        response = response.toLowerCase(Locale.ROOT);
        askedAboutDate = false;

        if (response.contains("belum") || response.contains("gak") || response.contains("enggak") ||
                response.contains("tidak") || response.contains("blm") || response.contains("ga") ||
                response.contains("ngga") || response.contains("single") || response.contains("jomblo")) {

            addBotMessage(new OutfitRecommendation(
                    "Udah gede masa belum punya pasangan 😂 wkwkwk",
                    null, null
            ));

            // Add a small delay before sending outfit recommendation
            new Handler().postDelayed(() -> {
                OutfitRecommendation recommendation = new OutfitRecommendation(
                        "Outfit untuk kencan:\n• Pria: Kemeja santai + jeans bersih\n• Wanita: Dress casual / blus + rok/celana chic\n• Sepatu nyaman tapi stylish",
                        "https://cf.shopee.co.id/file/0c5adfd8c5c3285f965fa5f1e0f6c41f",
                        "https://shopee.co.id/search?keyword=outfit%20kencan"
                );
                addBotMessage(recommendation);
            }, 1500);

        } else {
            OutfitRecommendation recommendation = new OutfitRecommendation(
                    "Outfit untuk kencan:\n• Pria: Kemeja santai + jeans bersih\n• Wanita: Dress casual / blus + rok/celana chic\n• Sepatu nyaman tapi stylish",
                    "https://cf.shopee.co.id/file/0c5adfd8c5c3285f965fa5f1e0f6c41f",
                    "https://shopee.co.id/search?keyword=outfit%20kencan"
            );
            addBotMessage(recommendation);
        }
    }

    private OutfitRecommendation generateResponse(String query) {
        query = query.toLowerCase(Locale.ROOT);

        String greetingResponse = getGreetingResponse(query);
        if (greetingResponse != null) {
            return new OutfitRecommendation(greetingResponse, null, null);
        }

        String slangResponse = getSlangResponse(query);
        if (slangResponse != null) {
            return new OutfitRecommendation(slangResponse, null, null);
        }

        if (query.contains("kampus") || query.contains("kuliah")) {
            return new OutfitRecommendation(
                    "Rekomendasi outfit ke kampus:\n• Kaos / kemeja kasual\n• Jeans / chino\n• Sneakers nyaman\n• Tas simpel",
                    "https://cf.shopee.co.id/file/1e54384c6d282d75e7c672ab8d7c1a8a",
                    "https://shopee.co.id/search?keyword=outfit%20kampus"
            );
        } else if (query.contains("wawancara")) {
            return new OutfitRecommendation(
                    "Outfit wawancara kerja:\n• Kemeja formal\n• Celana bahan\n• Sepatu pantofel\n• Blazer (opsional)",
                    "https://cf.shopee.co.id/file/6c6eddd28a7072e5fd08b6b90509a8e3",
                    "https://shopee.co.id/search?keyword=outfit%20wawancara"
            );
        } else if (query.contains("gunung") || query.contains("mendaki")) {
            return new OutfitRecommendation(
                    "Outfit ke gunung:\n• Jaket windbreaker atau waterproof\n• Celana cargo\n• Sepatu hiking\n• Topi / buff",
                    "https://cf.shopee.co.id/file/97ee3db96351c7987bd0c4d8e7b70a22",
                    "https://shopee.co.id/search?keyword=outfit%20gunung"
            );
        } else if (query.contains("ngelamar") || query.contains("lamar cewe") || query.contains("lamaran")) {
            return new OutfitRecommendation(
                    "Outfit untuk ngelamar cewe:\n• Kemeja putih atau pastel\n• Celana bahan rapi\n• Sepatu formal\n• Jam tangan elegan",
                    "https://cf.shopee.co.id/file/6462b5d4a7b276d5df43b14d9d2b21e7",
                    "https://shopee.co.id/search?keyword=outfit%20lamaran"
            );
        } else if (query.contains("cafe") || query.contains("nongkrong")) {
            return new OutfitRecommendation(
                    "Outfit ke cafe:\n• Kemeja santai atau sweater\n• Jeans slim fit\n• Sepatu sneakers stylish\n• Aksesori minimalis",
                    "https://cf.shopee.co.id/file/ab2e4e5eb9c3b4f0b3a3dfc29b4b1a47",
                    "https://shopee.co.id/search?keyword=outfit%20ke%20cafe"
            );
        } else if (query.contains("bioskop") || query.contains("nonton") || query.contains("cinema")) {
            return new OutfitRecommendation(
                    "Outfit nonton bioskop:\n• Kaos nyaman / kemeja casual\n• Jaket ringan (AC bioskop suka dingin)\n• Celana jeans / chino\n• Sneakers casual",
                    "https://cf.shopee.co.id/file/9b90d8f7ce40e6cde8096e77d8d38480",
                    "https://shopee.co.id/search?keyword=outfit%20casual"
            );
        } else if (query.contains("musim dingin") || query.contains("winter")) {
            return new OutfitRecommendation(
                    "Outfit musim dingin:\n• Jaket tebal / parka\n• Sweater\n• Scarf & sarung tangan\n• Boots",
                    "https://cf.shopee.co.id/file/32bd19db19d8a2b3ff8ac6b830ec43cf",
                    "https://shopee.co.id/search?keyword=outfit%20musim%20dingin"
            );
        } else if (query.contains("pantai") || query.contains("laut") || query.contains("beach")) {
            return new OutfitRecommendation(
                    "Outfit ke pantai:\n• Pakaian renang\n• Kemeja tipis\n• Celana pendek\n• Sandal jepit",
                    "https://cf.shopee.co.id/file/6c6eddd28a7072e5fd08b6b90509a8e3",
                    "https://shopee.co.id/search?keyword=outfit%20pantai"
            );
        } else if (query.contains("akad nikah") || query.contains("akad")) {
            return new OutfitRecommendation(
                    "Outfit akad nikah:\n• Pria: Kemeja putih / koko putih, celana bahan, peci.\n• Wanita: Kebaya putih sederhana, rok batik atau rok panjang.",
                    "https://cf.shopee.co.id/file/f5a7c9515edb7eeb67677c8f89a9c658",
                    "https://shopee.co.id/search?keyword=outfit%20akad%20nikah"
            );
        } else if (query.contains("kondangan") || query.contains("resepsi")) {
            return new OutfitRecommendation(
                    "Outfit kondangan:\n• Pria: Batik lengan panjang + celana bahan.\n• Wanita: Dress formal / kebaya modern.\n• Sepatu formal untuk tampil maksimal.",
                    "https://cf.shopee.co.id/file/7be9d5c5a6d6a46f9d909b285de46b37",
                    "https://shopee.co.id/search?keyword=outfit%20kondangan"
            );
        } else if (query.contains("musim panas") || query.contains("summer")) {
            return new OutfitRecommendation(
                    "Outfit musim panas:\n• Kaos katun tipis\n• Celana pendek\n• Topi dan kacamata hitam\n• Sandal atau sneakers ringan",
                    "https://cf.shopee.co.id/file/9b92d2c36e7c5b7f3e58f35e6a04c0f0",
                    "https://shopee.co.id/search?keyword=outfit%20musim%20panas"
            );
        } else if (query.contains("musim salju")) {
            return new OutfitRecommendation(
                    "Outfit musim salju:\n• Coat wol tebal\n• Sweater hangat\n• Scarf, sarung tangan, topi beanie\n• Boots tahan salju",
                    "https://cf.shopee.co.id/file/44320d97e5fa2416e7a3fc69bdb0fa1a",
                    "https://shopee.co.id/search?keyword=outfit%20musim%20salju"
            );
        } else if (query.contains("kantor") || query.contains("kerja")) {
            return new OutfitRecommendation(
                    "Outfit ke kantor:\n• Pria: Kemeja + celana bahan\n• Wanita: Blouse formal + rok/celana kerja\n• Sepatu pantofel atau flat shoes",
                    "https://cf.shopee.co.id/file/13b5f276c4e8d2bca4d8a680f4cc5198",
                    "https://shopee.co.id/search?keyword=outfit%20kantor"
            );
        } else if (query.contains("luar kota") || query.contains("travel") || query.contains("perjalanan")) {
            return new OutfitRecommendation(
                    "Outfit untuk keluar kota:\n• Pakaian santai & nyaman\n• Jaket ringan\n• Sepatu sneakers\n• Tas ransel simpel",
                    "https://cf.shopee.co.id/file/709f6b7a1cf60e4f9b432f8476d83442",
                    "https://shopee.co.id/search?keyword=outfit%20travel"
            );
        } else if (query.contains("keluarga") || query.contains("acara keluarga")) {
            return new OutfitRecommendation(
                    "Outfit acara keluarga:\n• Pria: Kemeja casual / batik santai\n• Wanita: Dress midi / blouse cantik\n• Sepatu rapi dan nyaman",
                    "https://cf.shopee.co.id/file/b5d5cf0de3cc93f3e8b457e6ff3a2c13",
                    "https://shopee.co.id/search?keyword=outfit%20keluarga"
            );
        } else if (query.contains("lebaran") || query.contains("idul fitri")) {
            return new OutfitRecommendation(
                    "Outfit Lebaran:\n• Pria: Baju koko + sarung atau celana panjang\n• Wanita: Gamis / tunik + kerudung cantik\n• Nuansa warna pastel atau putih",
                    "https://cf.shopee.co.id/file/58ffb3e6bd093e58223aa57e6b392abc",
                    "https://shopee.co.id/search?keyword=outfit%20lebaran"
            );
        } else if (query.contains("mall") || query.contains("jalan ke mall")) {
            return new OutfitRecommendation(
                    "Outfit ke mall:\n• T-shirt / blouse casual\n• Jeans / rok\n• Sneakers stylish\n• Tas kecil untuk praktis",
                    "https://cf.shopee.co.id/file/48717df5b95f0b88e6f312d6ff0544f7",
                    "https://shopee.co.id/search?keyword=outfit%20ke%20mall"
            );
        } else {
            return new OutfitRecommendation(
                    "Bisa sebutkan acara atau tempat spesifiknya? ✨",
                    null, null
            );
        }
    }

    private String getSlangResponse(String text) {
        String lower = text.toLowerCase(Locale.ROOT);
        List<String> slangWords = Arrays.asList("gua", "lo", "lu", "loe", "gw", "gweh");

        for (String slang : slangWords) {
            if (lower.contains(slang)) {
                if (lower.contains("lucu") || lower.contains("bercanda") || lower.contains("ketawa")) {
                    return "Wkwkwk santai aja bro, kalo butuh saran outfit bilang aja ya! 😎";
                }
                return "Santai aja bro! Butuh saran outfit apa nih? 😎";
            }
        }

        if (lower.contains("wkwk") || lower.contains("haha") || lower.contains("wkwkwk")) {
            return "Wkwkwk, kocak juga nih! Butuh saran outfit apa? 😂";
        }

        return null;
    }

    private String getGreetingResponse(String text) {
        String lower = text.toLowerCase(Locale.ROOT);
        List<String> pagiList = Arrays.asList("pagi", "pagii", "pgi");
        List<String> siangList = Arrays.asList("siang", "sng");
        List<String> soreList = Arrays.asList("sore", "sre");
        List<String> malamList = Arrays.asList("malam", "malem", "mlm");
        List<String> basicGreetings = Arrays.asList("halo", "hai", "hello");

        for (String greet : pagiList) {
            if (lower.contains(greet)) return "Selamat pagi juga! ☀️ Ada yang bisa aku bantu soal outfit?";
        }
        for (String greet : siangList) {
            if (lower.contains(greet)) return "Selamat siang! 🌞 Ada yang ingin ditanyakan tentang outfit?";
        }
        for (String greet : soreList) {
            if (lower.contains(greet)) return "Selamat sore! 🌇 Mau cari outfit buat sore ini?";
        }
        for (String greet : malamList) {
            if (lower.contains(greet)) return "Selamat malam! 🌙 Ada keperluan outfit malam ini?";
        }
        for (String greet : basicGreetings) {
            if (lower.contains(greet)) return "Hai juga! 👋 Ada yang bisa aku bantu soal outfit?";
        }
        return null;
    }

    private void addUserMessage(String message) {
        messageList.add(new Message(message, true));
        messageAdapter.notifyItemInserted(messageList.size() - 1);
        scrollToBottom();
    }

    private void addBotMessage(OutfitRecommendation rec) {
        if (rec == null) return;

        List<String> quickReplies = null;
        if ("Bisa sebutkan acara atau tempat spesifiknya? ✨".equals(rec.getDescription())) {
            quickReplies = Arrays.asList(
                    "Outfit ke kampus", "Outfit wawancara kerja", "Outfit musim dingin", "Outfit ke pantai",
                    "Outfit akad nikah", "Outfit kondangan", "Outfit musim panas", "Outfit musim salju",
                    "Outfit kencan/first date", "Outfit ke kantor", "Outfit ke luar kota", "Outfit acara keluarga",
                    "Outfit lebaran", "Outfit ke mall", "Outfit jalan sama cewe/cowo", "Outfit ke gunung",
                    "Outfit untuk ngelamar cewe", "Outfit untuk ke cafe", "Outfit untuk nonton bioskop"
            );
        }

        StringBuilder builder = new StringBuilder(rec.getDescription());
        if (rec.getImageUrl() != null) builder.append("\n\nLihat gambar: ").append(rec.getImageUrl());
        if (rec.getShopLink() != null) builder.append("\n\nCek di Shopee: ").append(rec.getShopLink());

        messageList.add(new Message(builder.toString(), false, quickReplies));
        messageAdapter.notifyItemInserted(messageList.size() - 1);
        scrollToBottom();
    }

    private void scrollToBottom() {
        chatRecyclerView.scrollToPosition(messageList.size() - 1);
    }

    private void showTypingIndicator(boolean show) {
        typingIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) animateDots();
    }

    private void animateDots() {
        int[] dotIds = {R.id.dot1, R.id.dot2, R.id.dot3};
        int delay = 0;
        for (int id : dotIds) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(findViewById(id), "translationY", 0f, -5f, 0f);
            animator.setDuration(900);
            animator.setStartDelay(delay);
            animator.setRepeatCount(ObjectAnimator.INFINITE);
            animator.start();
            delay += 150;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void sendQuickReply(String text) {
        addUserMessage(text);
        showTypingIndicator(true);

        new Handler().postDelayed(() -> {
            showTypingIndicator(false);

            // Check if query is about dating
            if (containsDateKeywords(text)) {
                askedAboutDate = true;
                addBotMessage(new OutfitRecommendation(
                        "Apakah kamu sudah punya pasangan? 😊",
                        null, null
                ));
                return;
            }

            OutfitRecommendation recommendation = generateResponse(text);
            addBotMessage(recommendation);
        }, 500);
    }
}