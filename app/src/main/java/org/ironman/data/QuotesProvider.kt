package org.ironman.data

data class Quote(
    val textEn: String,
    val textBn: String,
    val textAr: String,
    val author: String,
    val isHadith: Boolean = false
) {
    fun getText(language: String): String {
        return when (language) {
            "bn" -> textBn
            "ar" -> textAr
            else -> textEn
        }
    }
}

object QuotesProvider {
    val quotes = listOf(
        // Universal Hadiths on Self-Control, Character, Patience, and Effort
        Quote(
            textEn = "The strong person is not the good wrestler. The strong person is the one who controls himself in a fit of anger and desire.",
            textBn = "প্রকৃত শক্তিশালী সে নয় যে কুস্তিতে জিতে, বরং সে-ই প্রকৃত শক্তিশালী যে লালসা ও রাগের সময়ে নিজেকে ধরে রাখতে পারে।",
            textAr = "لَيْسَ الشَّدِيدُ بِالصُّرَعَةِ، إِنَّمَا الشَّدِيدُ الَّذِي يَمْلِكُ نَفْسَهُ عِنْدَ الْغَضَبِ",
            author = "Prophetic Wisdom (Hadith)",
            isHadith = true
        ),
        Quote(
            textEn = "Take advantage of five before five: your youth before your old age, your health before your sickness, your wealth before your poverty, your free time before your work, and your life before your death.",
            textBn = "পাঁচটি বিষয় আসার আগে পাঁচটি বিষয়ের সুযোগ নাও: বার্ধক্যের আগে যৌবনের, অসুস্থতার আগে সুস্থতার, দারিদ্র্যের আগে সচ্ছলতার, ব্যস্ততার আগে অবসরের এবং মৃত্যুর আগে জীবনের।",
            textAr = "اغْتَنِمْ خَمْسًا قَبْلَ خَمْسٍ: شَبَابَكَ قَبْلَ هَرَمِكَ، وَصِحَّتَكَ قَبْلَ سَقَمِكَ...",
            author = "Prophetic Wisdom (Hadith)",
            isHadith = true
        ),
        Quote(
            textEn = "The best of people are those with the best character and self-discipline.",
            textBn = "মানুষের মধ্যে সর্বোত্তম সে-ই, যার চরিত্র ও আত্মনিয়ন্ত্রণ সবচেয়ে সুন্দর।",
            textAr = "خِيَارُكُمْ أَحَاسِنُكُمْ أَخْلَاقًا",
            author = "Prophetic Wisdom (Hadith)",
            isHadith = true
        ),
        Quote(
            textEn = "Patience is light, and continuous effort brings accomplishment.",
            textBn = "ধৈর্য হলো আলো, আর ক্রমাগত প্রচেষ্টাই নিয়ে আসে সফলতা।",
            textAr = "الصَّبْرُ ضِيَاءٌ",
            author = "Prophetic Wisdom (Hadith)",
            isHadith = true
        ),
        Quote(
            textEn = "The most beloved deeds are those done consistently, even if they are small.",
            textBn = "সবচেয়ে পছন্দের কাজ হলো যা ধারাবাহিকভাবে করা হয়, এমনকি তা যদি ছোটও হয়।",
            textAr = "أَحَبُّ الأَعْمَالِ إِلَى اللَّهِ أَدْوَمُهَا وَإِنْ قَلَّ",
            author = "Prophetic Wisdom (Hadith)",
            isHadith = true
        ),
        Quote(
            textEn = "A good word is a act of charity, and restraint is honor.",
            textBn = "একটি ভালো কথা এবং নিজের ওপর নিয়ন্ত্রণ রাখা সম্মানের প্রতীক।",
            textAr = "الْكَلِمَةُ الطَّيِّبَةُ صَدَقَةٌ",
            author = "Prophetic Wisdom (Hadith)",
            isHadith = true
        ),

        // General Motivational Wisdom
        Quote(
            textEn = "He who conquers himself is the mightiest warrior.",
            textBn = "যে নিজেকে জয় করতে পারে, সে-ই সবচেয়ে বড় যোদ্ধা।",
            textAr = "من فتح نفسه فهو أعظم محارب",
            author = "Confucius"
        ),
        Quote(
            textEn = "Discipline is choosing between what you want now and what you want most.",
            textBn = "শৃঙ্খলা হলো এখনই কী চান আর আপনি জীবনে সবচেয়ে বেশি কী চান—তার মধ্যে সঠিকটা বেছে নেওয়া।",
            textAr = "الانضباط هو الاختيار بين ما تريد الآن وما تريد أكثر.",
            author = "Abraham Lincoln"
        ),
        Quote(
            textEn = "Iron is forged in fire. Your mind is forged through discipline.",
            textBn = "লোহা শক্ত হয় আগুনে পুড়ে, আর আপনার মন শক্ত হয় আত্মনিয়ন্ত্রণে।",
            textAr = "الحديد يصنع في النار، والعقل يصنع بالانضباط.",
            author = "Iron Will"
        ),
        Quote(
            textEn = "Small daily improvements over time lead to stunning results.",
            textBn = "প্রতিদিনের ছোট ছোট উন্নতি একসময় অভাবনীয় সাফল্য নিয়ে আসে।",
            textAr = "التحسينات اليومية الصغيرة تؤدي إلى نتائج مذهلة مع مرور الوقت.",
            author = "Robin Sharma"
        ),
        Quote(
            textEn = "Pain of discipline is temporary. Pain of regret lasts forever.",
            textBn = "আত্মনিয়ন্ত্রণের কষ্ট সাময়িক, কিন্তু অনুশোচনার কষ্ট সারাজীবনের।",
            textAr = "ألم الانضباط مؤقت، ولكن ألم الندم يدوم إلى الأبد.",
            author = "Jim Rohn"
        ),
        Quote(
            textEn = "Your will must be stronger than your desires.",
            textBn = "আপনার ইচ্ছাশক্তিকে হতে হবে আসক্তির চেয়েও শক্তিশালী।",
            textAr = "يجب أن تكون إرادتك أقوى من رغباتك.",
            author = "Anonymous"
        )
    )

    fun getRandomQuote(): Quote {
        return quotes.random()
    }
}
