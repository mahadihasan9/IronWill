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
            "bn" -> if (textBn.isNotBlank()) textBn else textEn
            "ar" -> if (textAr.isNotBlank()) textAr else textEn
            else -> textEn
        }
    }
}

object QuotesProvider {
    val quotes = listOf(
        // Prophetic Wisdom & Hadiths
        Quote(
            textEn = "The strong person is not the good wrestler. The strong person is the one who controls himself in a fit of anger and desire.",
            textBn = "প্রকৃত শক্তিশালী সে নয় যে কুস্তিতে জিতে, বরং সে-ই প্রকৃত শক্তিশালী যে লালসা ও রাগের সময়ে নিজেকে ধরে রাখতে পারে।",
            textAr = "لَيْسَ الشَّدِيدُ بِالصُّرَعَةِ، إِنَّمَا الشَّدِيدُ الَّذِي يَمْلِكُ نَفْسَهُ عِنْدَ الْغَضَبِ",
            author = "Prophetic Wisdom (Hadith)",
            isHadith = true
        ),
        Quote(
            textEn = "Take advantage of five before five: your youth before your old age, health before sickness, wealth before poverty, free time before work, and life before death.",
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
            textEn = "A good word is an act of charity, and restraint is honor.",
            textBn = "একটি ভালো কথা এবং নিজের ওপর নিয়ন্ত্রণ রাখা সম্মানের প্রতীক।",
            textAr = "الْكَلِمَةُ الطَّيِّبَةُ صَدَقَةٌ",
            author = "Prophetic Wisdom (Hadith)",
            isHadith = true
        ),

        // Willpower & Discipline Wisdom (50 Total Quotes)
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
        ),
        Quote(
            textEn = "Self-control is strength. Right thought is mastery. Calmness is power.",
            textBn = "আত্মনিয়ন্ত্রণই শক্তি। সঠিক চিন্তাই দক্ষতা। আর মানসিক প্রশান্তিই ক্ষমতা।",
            textAr = "ضبط النفس قوة، والتفكير الصحيح سيادة.",
            author = "James Allen"
        ),
        Quote(
            textEn = "Do not wait; the time will never be 'just right'. Start where you stand.",
            textBn = "অপেক্ষা করবেন না, কোনো সময়ই একদম 'সঠিক' হবে না। আপনি যেখানে আছেন সেখান থেকেই শুরু করুন।",
            textAr = "لا تنتظر، الوقت لن يكون مناسباً أبداً.",
            author = "Napoleon Hill"
        ),
        Quote(
            textEn = "We are what we repeatedly do. Excellence, then, is not an act, but a habit.",
            textBn = "আমরা প্রতিদিন যা করি, আমরা সেটাই হয়ে উঠি। তাই শ্রেষ্ঠত্ব কোনো কাজ নয়, এটি একটি অভ্যাস।",
            textAr = "نحن ما نفعله بتكرار. التميز إذن ليس عملاً، بل عادة.",
            author = "Aristotle"
        ),
        Quote(
            textEn = "Mastering others is strength. Mastering yourself is true power.",
            textBn = "অন্যকে জয় করা শক্তির পরিচয়, কিন্তু নিজেকে জয় করাই হলো প্রকৃত ক্ষমতা।",
            textAr = "السيطرة على الآخرين قوة، والسيطرة على النفس هي القوة الحقيقية.",
            author = "Lao Tzu"
        ),
        Quote(
            textEn = "The secret of change is to focus all of your energy not on fighting the old, but on building the new.",
            textBn = "পরিবর্তনের মূল রহস্য হলো পুরনোকে প্রতিহত করায় মনোযোগ না দিয়ে নতুন কিছু গড়ে তোলা।",
            textAr = "سر التغيير هو التركيز على بناء الجديد بدلاً من محاربة القديم.",
            author = "Socrates"
        ),
        Quote(
            textEn = "Don't count the days, make the days count.",
            textBn = "দিন গণনা করবেন না, প্রতিটি দিনকে মূল্যবান করে তুলুন।",
            textAr = "لا تعد الأيام، اجعل الأيام تعد.",
            author = "Muhammad Ali"
        ),
        Quote(
            textEn = "You have power over your mind - not outside events. Realize this, and you will find strength.",
            textBn = "আপনার নিজের মনের ওপর নিয়ন্ত্রণ আছে, বাইরের ঘটনার ওপর নয়। এটি বুঝতে পারলে আপনি মানসিক শক্তি পাবেন।",
            textAr = "لديك القوة على عقلك وليس الأحداث الخارجية.",
            author = "Marcus Aurelius"
        ),
        Quote(
            textEn = "Success is nothing more than a few simple disciplines, practiced every day.",
            textBn = "সফলতা আর কিছুই নয়, প্রতিদিন চর্চা করা কিছু সাধারণ সুশৃঙ্খল অভ্যাসের ফল।",
            textAr = "النجاح ليس أكثر من بضع انضباطات بسيطة يمارس كل يوم.",
            author = "Jim Rohn"
        ),
        Quote(
            textEn = "Action is the foundational key to all success.",
            textBn = "কাজ শুরু করাই হলো যেকোনো সাফল্যের মূল চাবিকাঠি।",
            textAr = "العمل هو المفتاح الأساسي لكل نجاح.",
            author = "Pablo Picasso"
        ),
        Quote(
            textEn = "The future depends on what you do today.",
            textBn = "আপনার ভবিষ্যৎ নির্ভর করে আজ আপনি কী করছেন তার ওপর।",
            textAr = "المستقبل يعتمد على ما تفعله اليوم.",
            author = "Mahatma Gandhi"
        ),
        Quote(
            textEn = "Believe you can and you're halfway there.",
            textBn = "বিশ্বাস করুন আপনি পারবেন, তবেই আপনার অর্ধেক পথ অতিক্রম হয়ে যাবে।",
            textAr = "إيماناً بأنك تستطيع يجعل الطريق منتصفه قطع بالفعل.",
            author = "Theodore Roosevelt"
        ),
        Quote(
            textEn = "Difficulties strengthen the mind, as labor does the body.",
            textBn = "কঠিন পরিস্থিতি মনকে শক্তিশালী করে, যেমন পরিশ্রম শরীরকে শক্ত করে।",
            textAr = "الصعوبات تقوي العقل كما يقوي العمل الجسد.",
            author = "Seneca"
        ),
        Quote(
            textEn = "Freedom is born of self-discipline.",
            textBn = "প্রকৃত স্বাধীনতা আসে আত্মনিয়ন্ত্রণ ও শৃঙ্খলার মধ্য দিয়ে।",
            textAr = "الحرية تولد من الانضباط الذاتي.",
            author = "Iron Will"
        ),
        Quote(
            textEn = "Every time you resist a weakness, you build a fortress within.",
            textBn = "প্রতিবার যখন আপনি কোনো দুর্বলতাকে জয় করেন, তখন আপনার ভেতরে একটি দুর্ভেদ্য দুর্গ তৈরি হয়।",
            textAr = "في كل مرة تقاوم فيها ضعفاً، تبني حصناً داخلياً.",
            author = "Iron Will"
        ),
        Quote(
            textEn = "Consistency is what transforms average into extraordinary.",
            textBn = "ধারাবাহিকতাই সাধারণকে অসাধারণে পরিণত করে।",
            textAr = "الاستمرارية هي ما يحول العادي إلى استثنائي.",
            author = "Anonymous"
        ),
        Quote(
            textEn = "Your life changes the moment you make a new, congruent, and committed decision.",
            textBn = "আপনার জীবন সেই মুহূর্তেই বদলে যায় যখন আপনি একটি দৃঢ় ও প্রতিশ্রুতিবদ্ধ সিদ্ধান্ত নেন।",
            textAr = "تتغير حياتك في اللحظة التي تتخذ فيها قراراً جاداً.",
            author = "Tony Robbins"
        ),
        Quote(
            textEn = "Through discipline comes freedom.",
            textBn = "শৃঙ্খলার মধ্য দিয়েই আসে মুক্তি।",
            textAr = "من خلال الانضباط تأتي الحرية.",
            author = "Aristotle"
        ),
        Quote(
            textEn = "It always seems impossible until it's done.",
            textBn = "যেকোনো কাজ শেষ না হওয়া পর্যন্ত অসম্ভব মনে হয়।",
            textAr = "يبدو الأمر دائماً مستحيلاً حتى يتم إنجازه.",
            author = "Nelson Mandela"
        ),
        Quote(
            textEn = "Continuous effort - not strength or intelligence - is the key to unlocking our potential.",
            textBn = "ক্রমাগত প্রচেষ্টাই হলো আমাদের মেধা ও শক্তির সর্বোচ্চ বিকাশ ঘটানোর চাবিকাঠি।",
            textAr = "الجهد المستمر هو المفتاح لإطلاق إمكانياتنا.",
            author = "Winston Churchill"
        ),
        Quote(
            textEn = "You don't have to be great to start, but you have to start to be great.",
            textBn = "শুরু করার জন্য মহান হওয়ার প্রয়োজন নেই, কিন্তু মহান হতে হলে শুরু করতে হবে।",
            textAr = "ليس عليك أن تكون عظيماً لتبدأ، ولكن عليك أن تبدأ لتكون عظيماً.",
            author = "Les Brown"
        ),
        Quote(
            textEn = "Great things are done by a series of small things brought together.",
            textBn = "ছোট ছোট অনেক কাজের ধারাবাহিক একত্রিত রূপই হলো মহৎ কোনো কাজ।",
            textAr = "الأشياء العظيمة تتحقق من خلال مجموعة من الأشياء الصغيرة.",
            author = "Vincent Van Gogh"
        ),
        Quote(
            textEn = "A river cuts through rock, not because of its power, but because of its persistence.",
            textBn = "নদী পাথর কেটে পথ বানায় তার শক্তির জন্য নয়, বরং তার অবিচল ধৈর্যের জন্য।",
            textAr = "الينبوع يثقب الصخر ليس بالقوة بل بالمثابرة.",
            author = "James N. Watkins"
        ),
        Quote(
            textEn = "Step by step, day by day, victory is forged in silence.",
            textBn = "ধাপে ধাপে, দিনে দিনে, নীরব সাধনাতেই বিজয় অর্জিত হয়।",
            textAr = "خطوة بخطوة، يوماً بعد يوم، يتشكل النصر في صمت.",
            author = "Iron Will"
        ),
        Quote(
            textEn = "Control your mind or it will control you.",
            textBn = "নিজের মনকে শাসন করুন, তা না হলে মনই আপনাকে চালিত করবে।",
            textAr = "سيطر على عقلك وإلا فسيطر عليك.",
            author = "Horace"
        ),
        Quote(
            textEn = "Fallback is a lesson, not a defeat. Stand up and conquer again.",
            textBn = "সাময়িক হোঁচট খাওয়া পরাজিত হওয়া নয়, এটি একটি শিক্ষা। আবার উঠে দাঁড়ান এবং জয় করুন।",
            textAr = "التعثر درس وليس هزيمة. قم وغزُ من جديد.",
            author = "Iron Will"
        ),
        Quote(
            textEn = "The difference between who you are and who you want to be is what you do.",
            textBn = "আপনি এখন কে আর আপনি ভবিষ্যতে কী হতে চান—তার ব্যবধান হলো আজ আপনি কী করছেন।",
            textAr = "الفرق بين من أنت ومن تريد أن تكون هو ما تفعله.",
            author = "Anonymous"
        ),
        Quote(
            textEn = "Be stronger than your strongest excuse.",
            textBn = "আপনার সবচেয়ে বড় অজুহাতের চেয়েও নিজের ইচ্ছাশক্তিকে শক্তিশালী করুন।",
            textAr = "كن أقوى من أقدم أعذارك.",
            author = "Anonymous"
        ),
        Quote(
            textEn = "With self-discipline, almost anything is possible.",
            textBn = "আত্মনিয়ন্ত্রণ ও নিষ্ঠা থাকলে প্রায় যেকোনো কিছুই অর্জন করা সম্ভব।",
            textAr = "بالانضباط الذاتي، كل شيء ممكن تقريباً.",
            author = "Theodore Roosevelt"
        ),
        Quote(
            textEn = "A champion is defined by how they recover from failure.",
            textBn = "একজন প্রকৃত বিজয়ীকে চেনা যায় ব্যর্থতার পর সে কীভাবে ঘুরে দাঁড়ায় তা দেখে।",
            textAr = "البطل يتحدد بكيفية تعافيه من الفشل.",
            author = "Anonymous"
        ),
        Quote(
            textEn = "Focus on the process, and the results will take care of themselves.",
            textBn = "প্রচেষ্টার প্রক্রিয়ায় মনোযোগ দিন, ফলাফলের স্থান আপনাতেই তৈরি হয়ে যাবে।",
            textAr = "ركز على العملية، والنتائج ستأتي بنفسها.",
            author = "Anonymous"
        ),
        Quote(
            textEn = "One day or Day One. You decide.",
            textBn = "'একদিন করব' নাকি 'আজই প্রথম দিন'—সিদ্ধান্ত আপনার।",
            textAr = "يوماً ما أو اليوم الأول. أنت تقرر.",
            author = "Anonymous"
        ),
        Quote(
            textEn = "Patience, persistence, and perspiration make an unbeatable combination for success.",
            textBn = "ধৈর্য, অধ্যবসায় এবং কঠোর পরিশ্রম—সাফল্যের এক অপরাজেয় শক্তি।",
            textAr = "الصبر والمثابرة والجهد تجعل النجاح لا يقهر.",
            author = "Napoleon Hill"
        ),
        Quote(
            textEn = "Your habits define your destiny.",
            textBn = "আপনার অভ্যাসের ওপরই গড়ে ওঠে আপনার ভবিষ্যৎ ভাগ্য।",
            textAr = "عاداتك تحدد مصيرك.",
            author = "Anonymous"
        ),
        Quote(
            textEn = "Build an iron mind, and no impulse can shatter your peace.",
            textBn = "লোহার মতো শক্ত মন গড়ে তুলুন, কোনো সাময়িক প্রলোভনই আপনার শান্তি ভাঙতে পারবে না।",
            textAr = "ابنِ عقلاً من حديد، ولن تتمكن أي نزوة من تدمير سلامك.",
            author = "Iron Will"
        ),
        Quote(
            textEn = "Work hard in silence, let your success be your noise.",
            textBn = "নীরবে কঠোর পরিশ্রম করুন, আপনার সাফল্যকেই আওয়াজ তুলতে দিন।",
            textAr = "اعمل بجد في صمت، ودع نجاحك يحدث الضجيج.",
            author = "Frank Ocean"
        ),
        Quote(
            textEn = "Motivation gets you going, but discipline keeps you growing.",
            textBn = "অনুপ্রেরণা আপনাকে কাজ শুরু করায়, কিন্তু শৃঙ্খলা আপনাকে এগিয়ে নিয়ে যায়।",
            textAr = "الدافع يجعلك تبدأ، لكن الانضباط يجعلك تستمر وتنمو.",
            author = "John C. Maxwell"
        ),
        Quote(
            textEn = "Your future self will thank you for the sacrifices you make today.",
            textBn = "আজ আপনার করা ত্যাগের জন্য আপনার ভবিষ্যতের সত্ত্বা একদিন আপনাকে ধন্যবাদ দেবে।",
            textAr = "ذاتك في المستقبل ستشكرك على التضحيات التي تقدمها اليوم.",
            author = "Iron Will"
        )
    )

    fun getRandomQuote(): Quote {
        return quotes.random()
    }
}
