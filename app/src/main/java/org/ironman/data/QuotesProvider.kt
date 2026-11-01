package org.ironman.data

data class Quote(
    val textEn: String,
    val textBn: String = "",
    val textAr: String = "",
    val textHi: String = "",
    val textUr: String = "",
    val textEs: String = "",
    val textRu: String = "",
    val textZh: String = "",
    val textJa: String = "",
    val author: String,
    val isHadith: Boolean = false
) {
    fun getText(language: String): String {
        val result = when (language) {
            "bn" -> textBn
            "ar" -> textAr
            "hi" -> textHi
            "ur" -> textUr
            "es" -> textEs
            "ru" -> textRu
            "zh" -> textZh
            "ja" -> textJa
            else -> textEn
        }
        return if (result.isNotBlank()) result else textEn
    }
}

object QuotesProvider {
    val quotes = listOf(
        // Prophetic Wisdom & Hadiths
        Quote(
            textEn = "The strong person is not the good wrestler. The strong person is the one who controls himself in a fit of anger and desire.",
            textBn = "প্রকৃত শক্তিশালী সে নয় যে কুস্তিতে জিতে, বরং সে-ই প্রকৃত শক্তিশালী যে লালসা ও রাগের সময়ে নিজেকে ধরে রাখতে পারে।",
            textAr = "لَيْسَ الشَّدِيدُ بِالصُّرَعَةِ، إِنَّمَا الشَّدِيدُ الَّذِي يَمْلِكُ نَفْسَهُ عِنْدَ الْغَضَبِ",
            textHi = "सच्चा पहलवान वह नहीं जो कुश्ती जीतता है, बल्कि शक्तिशाली वह है जो क्रोध और वासना में खुद पर नियंत्रण रखता है।",
            textUr = "پہلوان وہ نہیں جو کشتی میں جیت جائے، بلکہ طاقتور وہ ہے جو غصے اور خواہش کے وقت خود پر قابو رکھے۔",
            textEs = "El fuerte no es el buen luchador. El fuerte es quien se controla a sí mismo en un momento de ira y deseo.",
            textRu = "Силён не тот, кто побеждает в борьбе, а тот, кто управляет собой в гневе и страстях.",
            textZh = "真正的强者不是摔跤的高手，而是在愤怒和欲望面前能够克制自己的人。",
            textJa = "真の強者とはレスリングで勝つ者ではなく、怒りや欲望に直面した時に自分を制御できる者である。",
            author = "Prophetic Wisdom (Hadith)",
            isHadith = true
        ),
        Quote(
            textEn = "Take advantage of five before five: your youth before your old age, health before sickness, wealth before poverty, free time before work, and life before death.",
            textBn = "পাঁচটি বিষয় আসার আগে পাঁচটি বিষয়ের সুযোগ নাও: বার্ধক্যের আগে যৌবনের, অসুস্থতার আগে সুস্থতার, দারিদ্র্যের আগে সচ্ছলতার, ব্যস্ততার আগে অবসরের এবং মৃত্যুর আগে জীবনের।",
            textAr = "اغْتَنِمْ خَمْسًا قَبْلَ خَمْسٍ: شَبَابَكَ قَبْلَ هَرَمِكَ، وَصِحَّتَكَ قَبْلَ سَقَمِكَ...",
            textHi = "पाँच चीज़ों से पहले पाँच चीज़ों का लाभ उठाएँ: बुढ़ापे से पहले जवानी, बीमारी से पहले सेहत, गरीबी से पहले धन, काम से पहले फुर्सत, और मौत से पहले ज़िंदगी।",
            textUr = "پانچ چیزوں کو پانچ سے پہلے غنیمت جانو: بڑھاپے سے پہلے جوانی، بیماری سے پہلے صحت، غربت سے پہلے مالداری، مصروفیت سے پہلے فراغت، اور موت سے پہلے زندگی۔",
            textEs = "Aprovecha cinco cosas antes de cinco: tu juventud antes de tu vejez, salud antes de enfermedad, riqueza antes de pobreza, tiempo libre antes de trabajo, y vida antes de muerte.",
            textRu = "Воспользуйтесь пятью вещами до наступления пяти других: молодостью до старости, здоровьем до болезни, богатством до бедности, свободным временем до занятости и жизнью до смерти.",
            textZh = "趁这五种情况到来之前好好利用这五种财富：老迈之前的青春，疾病之前的健康，贫穷之前的财富，忙碌之前的空闲，死亡之前的生命。",
            textJa = "老いる前の若さ、病む前の健康、貧しくなる前の富、忙しくなる前の暇、死ぬ前の生という5つの価値を活用せよ。",
            author = "Prophetic Wisdom (Hadith)",
            isHadith = true
        ),
        Quote(
            textEn = "The best of people are those with the best character and self-discipline.",
            textBn = "মানুষের মধ্যে সর্বোত্তম সে-ই, যার চরিত্র ও আত্মনিয়ন্ত্রণ সবচেয়ে সুন্দর।",
            textAr = "خِيَارُكُمْ أَحَاسِنُكُمْ أَخْلَاقًا",
            textHi = "लोगों में सबसे अच्छे वे हैं जिनका चरित्र और आत्म-अनुशासन सबसे बेहतर है।",
            textUr = "لوگوں میں سب سے بہترین وہ ہیں جن کا اخلاق اور خود ضبطی سب سے بہتر ہے۔",
            textEs = "Los mejores de las personas son aquellos con el mejor carácter y autodisciplina.",
            textRu = "Лучшие из людей — это те, кто обладает лучшим характером и самодисциплиной.",
            textZh = "人中最优秀者，乃品德与自律最臻完美者。",
            textJa = "最高の人々とは、最高の品格と自律性を持つ人々である。",
            author = "Prophetic Wisdom (Hadith)",
            isHadith = true
        ),
        Quote(
            textEn = "Patience is light, and continuous effort brings accomplishment.",
            textBn = "ধৈর্য হলো আলো, আর ক্রমাগত প্রচেষ্টাই নিয়ে আসে সফলতা।",
            textAr = "الصَّبْرُ ضِيَاءٌ",
            textHi = "धैर्य प्रकाश है, और निरंतर प्रयास सफलता लाता है।",
            textUr = "صبر روشنی ہے، اور مسلسل کوشش کامیابی لاتی ہے۔",
            textEs = "La paciencia es luz, y el esfuerzo continuo trae logros.",
            textRu = "Терпение — это свет, а непрерывные усилия приносят успех.",
            textZh = "忍耐是光明，不懈的努力带来成就。",
            textJa = "忍耐は光であり、継続的な努力が成就をもたらす。",
            author = "Prophetic Wisdom (Hadith)",
            isHadith = true
        ),
        Quote(
            textEn = "The most beloved deeds are those done consistently, even if they are small.",
            textBn = "সবচেয়ে পছন্দের কাজ হলো যা ধারাবাহিকভাবে করা হয়, এমনকি তা যদি ছোটও হয়।",
            textAr = "أَحَبُّ الأَعْمَالِ إِلَى اللَّهِ أَدْوَمُهَا وَإِنْ قَلَّ",
            textHi = "सबसे प्रिय कार्य वे हैं जो लगातार किए जाते हैं, भले ही वे छोटे हों।",
            textUr = "سب سے پسندیدہ اعمال وہ ہیں جو باقاعدگی سے کیے جائیں، چاہے وہ چھوٹے ہی کیوں نہ ہوں۔",
            textEs = "Las obras más amadas son las que se hacen con constancia, aunque sean pequeñas.",
            textRu = "Самые любимые дела — это те, которые совершаются постоянно, даже если они малы.",
            textZh = "最令人喜爱的善行是持之以恒的善行，哪怕微不足道。",
            textJa = "最も愛される行いとは、たとえ小さくても継続して行われるものである。",
            author = "Prophetic Wisdom (Hadith)",
            isHadith = true
        ),

        // Willpower & Discipline Wisdom
        Quote(
            textEn = "He who conquers himself is the mightiest warrior.",
            textBn = "যে নিজেকে জয় করতে পারে, সে-ই সবচেয়ে বড় যোদ্ধা।",
            textAr = "من فتح نفسه فهو أعظم محارب",
            textHi = "जो खुद पर विजय प्राप्त करता है, वही सबसे बड़ा योद्धा है।",
            textUr = "جو خود پر فتح حاصل کرتا ہے وہی سب سے بڑا جنگجو ہے۔",
            textEs = "Quien se vence a sí mismo es el guerrero más poderoso.",
            textRu = "Тот, кто побеждает себя — самый могущественный воин.",
            textZh = "胜人者有力，自胜者强。",
            textJa = "己に打ち克つ者こそ、最も強大な战士である。",
            author = "Confucius"
        ),
        Quote(
            textEn = "Discipline is choosing between what you want now and what you want most.",
            textBn = "শৃঙ্খলা হলো এখনই কী চান আর আপনি জীবনে সবচেয়ে বেশি কী চান—তার মধ্যে সঠিকটা বেছে নেওয়া।",
            textAr = "الانضباط هو الاختيار بين ما تريد الآن وما تريد أكثر.",
            textHi = "अनुशासन वह चुनना है जो आप अभी चाहते हैं और जो आप सबसे अधिक चाहते हैं।",
            textUr = "انضباط وہ انتخاب ہے جو آپ ابھی چاہتے ہیں اور جو آپ سب سے زیادہ چاہتے ہیں۔",
            textEs = "La disciplina es elegir entre lo que quieres ahora y lo que más quieres.",
            textRu = "Дисциплина — это выбор между тем, чего вы хотите сейчас, и тем, чего вы хотите больше всего.",
            textZh = "自律，就是在“眼前所想”与“终极所求”之间做出选择。",
            textJa = "自律とは、「今欲しいもの」と「最も欲しいもの」のどちらかを選択することである。",
            author = "Abraham Lincoln"
        ),
        Quote(
            textEn = "Iron is forged in fire. Your mind is forged through discipline.",
            textBn = "লোহা শক্ত হয় আগুনে পুড়ে, আর আপনার মন শক্ত হয় আত্মনিয়ন্ত্রণে।",
            textAr = "الحديد يصنع في النار، والعقل يصنع بالانضباط.",
            textHi = "लोहा आग में ढलता है। आपका मन अनुशासन से मजबूत होता है।",
            textUr = "لوہا آگ میں ڈھلتا ہے۔ آپ کا ذہن انضباط سے مضبوط ہوتا ہے۔",
            textEs = "El hierro se forja en el fuego. Tu mente se forja a través de la disciplina.",
            textRu = "Железо коварно в огне. Твой разум коварно дисциплиной.",
            textZh = "百炼成钢，百自律精明。",
            textJa = " 鉄は火で鍛えられ、心は自律によって鍛えられる。",
            author = "Iron Will"
        ),
        Quote(
            textEn = "Small daily improvements over time lead to stunning results.",
            textBn = "প্রতিদিনের ছোট ছোট উন্নতি একসময় অভাবনীয় সাফল্য নিয়ে আসে।",
            textAr = "التحسينات اليومية الصغيرة تؤدي إلى نتائج مذهلة مع مرور الوقت.",
            textHi = "समय के साथ दैनिक छोटे-छोटे सुधार आश्चर्यजनक परिणाम लाते हैं।",
            textUr = "وقت کے ساتھ ساتھ روزانہ کی چھوٹی بہتری شاندار نتائج لاتی ہے۔",
            textEs = "Las pequeñas mejoras diarias con el tiempo conducen a resultados deslumbrantes.",
            textRu = "Небольшие ежедневные улучшения со временем приводят к потрясающим результатам.",
            textZh = "日积月累的微小进步最终将汇聚成令人惊叹的巨变。",
            textJa = "毎日の小さな改善の積み重ねが、やがて目を見張るような大きな成果をもたらす。",
            author = "Robin Sharma"
        ),
        Quote(
            textEn = "Pain of discipline is temporary. Pain of regret lasts forever.",
            textBn = "আত্মনিয়ন্ত্রণের কষ্ট সাময়িক, কিন্তু অনুশোচনার কষ্ট সারাজীবনের।",
            textAr = "ألم الانضباط مؤقت، ولكن ألم الندم يدوم إلى الأبد.",
            textHi = "अनुशासन का दर्द दर्द अस्थायी है। पछतावे का दर्द हमेशा रहता है।",
            textUr = "انضباط کا درد عارضی ہوتا ہے۔ پچھتاوے کا درد ہمیشہ رہتا ہے۔",
            textEs = "El dolor de la disciplina es temporal. El dolor del arrepentimiento dura para siempre.",
            textRu = "Боль дисциплины временна. Боль сожаления длится вечно.",
            textZh = "自律的痛苦是暂时的，而后悔的痛苦却是长久的。",
            textJa = "規律の痛みは一時的だが、後悔の痛みは永遠に続く。",
            author = "Jim Rohn"
        ),
        Quote(
            textEn = "Your will must be stronger than your desires.",
            textBn = "আপনার ইচ্ছাশক্তিকে হতে হবে আসক্তির চেয়েও শক্তিশালী।",
            textAr = "يجب أن تكون إرادتك أقوى من رغباتك.",
            textHi = "आपकी इच्छाशक्ति आपकी इच्छाओं से अधिक मजबूत होनी चाहिए।",
            textUr = "آپ کا ارادہ آپ کی خواہشات سے زیادہ مضبوط ہونا چاہئے۔",
            textEs = "Tu voluntad debe ser más fuerte que tus deseos.",
            textRu = "Твоя воля должна быть сильнее твоих желаний.",
            textZh = "你的意志必须胜过你的欲望。",
            textJa = "あなたの意志は、欲望よりも強くなければならない。",
            author = "Anonymous"
        ),
        Quote(
            textEn = "Self-control is strength. Right thought is mastery. Calmness is power.",
            textBn = "আত্মনিয়ন্ত্রণই শক্তি। সঠিক চিন্তাই দক্ষতা। আর মানসিক প্রশান্তিই ক্ষমতা।",
            textAr = "ضبط النفس قوة، والتفكير الصحيح سيادة.",
            textHi = "आत्म-नियंत्रण ताकत है। सही सोच महारत है। शांति शक्ति है।",
            textUr = "خود ضبطی طاقت ہے۔ صحیح سوچ مہارت ہے۔ سکون طاقت ہے۔",
            textEs = "El autocontrol es fuerza. El pensamiento correcto es maestría. La calma es poder.",
            textRu = "Самообладание — это сила. Правильные мысли — это мастерство. Спокойствие — это power.",
            textZh = "自控是力量，正念是精通，平静是魄力。",
            textJa = "克己心は強さであり、正しい思考は熟達であり、平静さは力である。",
            author = "James Allen"
        ),
        Quote(
            textEn = "Mastering others is strength. Mastering yourself is true power.",
            textBn = "অন্যকে জয় করা শক্তির পরিচয়, কিন্তু নিজেকে জয় করাই হলো প্রকৃত ক্ষমতা।",
            textAr = "السيطرة على الآخرين قوة، والسيطرة على النفس هي القوة الحقيقية.",
            textHi = "दूसरों पर विजय पाना ताकत है। खुद पर विजय पाना असली शक्ति है।",
            textUr = "دوسروں پر قابو پانا طاقت ہے۔ خود پر قابو پانا سچی طاقت ہے۔",
            textEs = "Dominar a otros es fuerza. Dominarte a ti mismo es verdadero poder.",
            textRu = "Победа над другими — это сила. Победа над собой — настоящая власть.",
            textZh = "胜人者有力，自胜者强。",
            textJa = "他者を制する者は力あり、己に打ち克つ者こそ真の強者なり。",
            author = "Lao Tzu"
        ),
        Quote(
            textEn = "Don't count the days, make the days count.",
            textBn = "দিন গণনা করবেন না, প্রতিটি দিনকে মূল্যবান করে তুলুন।",
            textAr = "لا تعد الأيام، اجعل الأيام تعد.",
            textHi = "दिन मत गिनो, दिनों को सार्थक बनाओ।",
            textUr = "دن نہ گنیں، دنوں کو باکمال بنائیں۔",
            textEs = "No cuentes los días, haz que los días cuenten.",
            textRu = "Не считай дни, сделай так, чтобы каждый день имел значение.",
            textZh = "不要计算日子，要让每一天都有意义。",
            textJa = "日数を数えるな。一日一日を意味あるものにせよ。",
            author = "Muhammad Ali"
        ),
        Quote(
            textEn = "Success is nothing more than a few simple disciplines, practiced every day.",
            textBn = "সফলতা আর কিছুই নয়, প্রতিদিন চর্চা করা কিছু সাধারণ সুশৃঙ্খল অভ্যাসের ফল।",
            textAr = "النجاح ليس أكثر من بضع انضباطات بسيطة يمارس كل يوم.",
            textHi = "सफलता हर दिन अभ्यास किए जाने वाले कुछ सरल अनुशासनों से बढ़कर कुछ नहीं है।",
            textUr = "کامیابی روزانہ کی جانے والی چند سادہ عادتوں کے سوا کچھ نہیں ہے۔",
            textEs = "El éxito no es más que unas pocas disciplinas simples, practicadas cada día.",
            textRu = "Успех — это не более чем несколько простых дисциплин, практикуемых каждый день.",
            textZh = "成功不过是每天坚持执行几项简单的自律准则。",
            textJa = "成功とは、毎日実践されるいくつかの単純な自律の積み重ねにすぎない。",
            author = "Jim Rohn"
        ),
        Quote(
            textEn = "Action is the foundational key to all success.",
            textBn = "কাজ শুরু করাই হলো যেকোনো সাফল্যের মূল চাবিকাঠি।",
            textAr = "العمل هو المفتاح الأساسي لكل نجاح.",
            textHi = "कार्रवाई ही सभी सफलता की बुनियादी कुंजी है।",
            textUr = "عمل تمام کامیابیوں کی بنیادی چابی ہے۔",
            textEs = "La acción es la clave fundamental de todo éxito.",
            textRu = "Действие — основной ключ к любому успеху.",
            textZh = "行动是一切成功的基石。",
            textJa = "行動こそが、あらゆる成功への根幹となる鍵である。",
            author = "Pablo Picasso"
        ),
        Quote(
            textEn = "Believe you can and you're halfway there.",
            textBn = "বিশ্বাস করুন আপনি পারবেন, তবেই আপনার অর্ধেক পথ অতিক্রম হয়ে যাবে।",
            textAr = "إيماناً بأنك تستطيع يجعل الطريق منتصفه قطع بالفعل.",
            textHi = "विश्वास करें कि आप कर सकते हैं और आप आधा रास्ता तय कर चुके हैं।",
            textUr = "یقین رکھیں کہ آپ کر سکتے ہیں اور آپ کا آدھا راستہ طے ہو چکا ہے۔",
            textEs = "Cree que puedes y ya estás a medio camino.",
            textRu = "Поверь, что можешь, и ты уже на полпути.",
            textZh = "相信你自己能做到，你就已经成功了一半。",
            textJa = "できると信じれば、あなたはすでに半ば達成している。",
            author = "Theodore Roosevelt"
        ),
        Quote(
            textEn = "Difficulties strengthen the mind, as labor does the body.",
            textBn = "কঠিন পরিস্থিতি মনকে শক্তিশালী করে, যেমন পরিশ্রম শরীরকে শক্ত করে।",
            textAr = "الصعوبات تقوي العقل كما يقوي العمل الجسد.",
            textHi = "कठिनाइयाँ मन को मजबूत करती हैं, जैसे श्रम शरीर को करता है।",
            textUr = "مشکلات ذہن کو مضبوط کرتی ہیں، جیسے محنت جسم کو کرتی ہے۔",
            textEs = "Las dificultades fortalecen la mente, como el trabajo lo hace con el cuerpo.",
            textRu = "Трудности укрепляют разум, так же как труд укрепляет тело.",
            textZh = "困难磨砺意志，正如劳作强健体魄。",
            textJa = "困難は心を強くする。労働が体を強くするように。",
            author = "Seneca"
        ),
        Quote(
            textEn = "Freedom is born of self-discipline.",
            textBn = "প্রকৃত স্বাধীনতা আসে আত্মনিয়ন্ত্রণ ও শৃঙ্খলার মধ্য দিয়ে।",
            textAr = "الحرية تولد من الانضباط الذاتي.",
            textHi = "स्वतंत्रता आत्म-अनुशासन से पैदा होती है।",
            textUr = "آزادی خود ضبطی سے جنم لیتی ہے۔",
            textEs = "La libertad nace de la autodisciplina.",
            textRu = "Свобода рождается из самодисциплины.",
            textZh = "自由源于高度的自律。",
            textJa = "自由は自律から生まれる。",
            author = "Iron Will"
        ),
        Quote(
            textEn = "Every time you resist a weakness, you build a fortress within.",
            textBn = "প্রতিবার যখন আপনি কোনো দুর্বলতাকে জয় করেন, তখন আপনার ভেতরে একটি দুর্ভেদ্য দুর্গ তৈরি হয়।",
            textAr = "في كل مرة تقاوم فيها ضعفاً، تبني حصناً داخلياً.",
            textHi = "हर बार जब आप किसी कमजोरी का विरोध करते हैं, तो आप भीतर एक किला बनाते हैं।",
            textUr = "ہر بار جب آپ کسی کمزوری کا مقابلہ کرتے ہیں، آپ اندر ایک قلعہ بناتے ہیں۔",
            textEs = "Cada vez que resistes una debilidad, construyes una fortaleza en tu interior.",
            textRu = "Каждый раз, когда ты сопротивляешься слабости, ты строишь крепость внутри себя.",
            textZh = "每当你战胜一次软弱，你就在内心建立起一座坚固的堡垒。",
            textJa = "弱さに打ち克つたびに、あなたの心の中に揺るぎない城塞が築かれる。",
            author = "Iron Will"
        ),
        Quote(
            textEn = "Consistency is what transforms average into extraordinary.",
            textBn = "ধারাবাহিকতাই সাধারণকে অসাধারণে পরিণত করে।",
            textAr = "الاستمرارية هي ما يحول العادي إلى استثنائي.",
            textHi = "निरंतरता ही औसत को असाधारण में बदलती है।",
            textUr = "باقاعدگی ہی عام کو غیر معمولی میں بدلتی ہے۔",
            textEs = "La constancia es lo que transforma lo promedio en extraordinario.",
            textRu = "Постоянство — это то, что превращает среднее в выдающееся.",
            textZh = "坚持不懈，能将平凡化为神奇。",
            textJa = "継続こそが、平凡さを非凡さへと変貌させる。",
            author = "Anonymous"
        ),
        Quote(
            textEn = "It always seems impossible until it's done.",
            textBn = "যেকোনো কাজ শেষ না হওয়া পর্যন্ত অসম্ভব মনে হয়।",
            textAr = "يبدو الأمر دائماً مستحيلاً حتى يتم إنجازه.",
            textHi = "यह हमेशा तब तक असंभव लगता है जब तक कि यह हो न जाए।",
            textUr = "یہ ہمیشہ تب تک ناممکن لگتا ہے جب تک کہ یہ ہو نہ جائے۔",
            textEs = "Siempre parece imposible hasta que se hace.",
            textRu = "Это всегда кажется невозможным, пока не будет сделано.",
            textZh = "事情未完成之前，一切看起来似乎都是不可能的。",
            textJa = "何事も達成するまでは不可能に思えるものだ。",
            author = "Nelson Mandela"
        ),
        Quote(
            textEn = "Control your mind or it will control you.",
            textBn = "নিজের মনকে শাসন করুন, তা না হলে মনই আপনাকে চালিত করবে।",
            textAr = "سيطر على عقلك وإلا فسيطر عليك.",
            textHi = "अपने मन पर नियंत्रण रखें अन्यथा यह आप पर नियंत्रण रखेगा।",
            textUr = "اپنے ذہن پر قابو رکھیں ورنہ یہ آپ پر قابو پا لے گا۔",
            textEs = "Controla tu mente o ella te controlará a ti.",
            textRu = "Управляй своим разумом, иначе он будет управлять тобой.",
            textZh = "掌控你的心智，否则它将掌控你。",
            textJa = "自分の心を制御せよ。さもなければ心に支配されることになる。",
            author = "Horace"
        ),
        Quote(
            textEn = "Be stronger than your strongest excuse.",
            textBn = "আপনার সবচেয়ে বড় অজুহাতের চেয়েও নিজের ইচ্ছাশক্তিকে শক্তিশালী করুন।",
            textAr = "كن أقوى من أقدم أعذارك.",
            textHi = "अपने सबसे मजबूत बहाने से भी ज्यादा मजबूत बनें।",
            textUr = "اپنے سب سے مضبوط بہانے سے بھی زیادہ مضبوط بنیں۔",
            textEs = "Sé más fuerte que tu excusa más fuerte.",
            textRu = "Будь сильнее своего самого сильного оправдания.",
            textZh = "要比你最强烈的借口更加强大。",
            textJa = "どんなに強い言い訳よりも、自分自身を強く保て。",
            author = "Anonymous"
        ),
        Quote(
            textEn = "With self-discipline, almost anything is possible.",
            textBn = "আত্মনিয়ন্ত্রণ ও নিষ্ঠা থাকলে প্রায় যেকোনো কিছুই অর্জন করা সম্ভব।",
            textAr = "بالانضباط الذاتي، كل شيء ممكن تقريباً.",
            textHi = "आत्म-अनुशासन से लगभग कुछ भी संभव है।",
            textUr = "خود ضبطی کے ساتھ تقریباً ہر چیز ممکن ہے۔",
            textEs = "Con autodisciplina, casi cualquier cosa es posible.",
            textRu = "С самодисциплиной возможно практически всё.",
            textZh = "有了自律，几乎没有什么是不可能的。",
            textJa = "高度な自律心があれば、ほぼすべてのことが可能になる。",
            author = "Theodore Roosevelt"
        ),
        Quote(
            textEn = "Build an iron mind, and no impulse can shatter your peace.",
            textBn = "লোহার মতো শক্ত মন গড়ে তুলুন, কোনো সাময়িক প্রলোভনই আপনার শান্তি ভাঙতে পারবে না।",
            textAr = "ابنِ عقلاً من حديد، ولن تتمكن أي نزوة من تدمير سلامك.",
            textHi = "एक लोहे जैसा मन बनाएं, और कोई भी आवेग आपकी शांति को भंग नहीं कर सकता।",
            textUr = "ایک لوہے جیسا ذہن بنائیں، اور کوئی بھی جبلت آپ کا سکون نہیں توڑ سکتی۔",
            textEs = "Construye una mente de hierro y ningún impulso podrá romper tu paz.",
            textRu = "Построй железный разум, и ни один импульс не сможет разрушить твой покой.",
            textZh = "打造如钢铁般坚韧的内心，任何一时的冲动都无法扰乱你的宁静。",
            textJa = "鉄のような意志を築けば、どんな衝動もあなたの平穏を乱すことはできない。",
            author = "Iron Will"
        )
    )

    fun getRandomQuote(): Quote {
        return quotes.random()
    }

    fun getRandomQuoteDifferentFrom(current: Quote? = null): Quote {
        if (current == null || quotes.size <= 1) return getRandomQuote()
        val filtered = quotes.filter { it != current }
        return filtered.random()
    }
}
