package kr.blugon.tjfinder.module

//import kr.bydelta.koala.assembleHangul
//import kr.bydelta.koala.getChosung
//import kr.bydelta.koala.getJongsung
//import kr.bydelta.koala.getJungsung
//
//fun String.convertToKoreanPronunciation(): String {
//    var result = ""
//
//    this.forEachIndexed { i, c ->
//        val beforeC = this.getOrNull(i-1)
//        val beforeR = result.getOrNull(result.length-1)
//        if(beforeR == null) {
//            result += (Hiragana.intonations[c]?: Katakana.intonations[c])?: c
//            return@forEachIndexed
//        }
//        when(c) {
//            'い','イ' -> {
//                result += if(
//                    Hiragana.Columns.e.contains(beforeC) ||
//                    Katakana.Columns.e.contains(beforeC)
//                ) '에'
//                else '이'
//            }
//            'う','ウ' -> {
//                result += if(
//                    Hiragana.Columns.o.contains(beforeC) ||
//                    Katakana.Columns.o.contains(beforeC)
//                ) '오'
//                else '우'
//            }
//
//            'は','ハ' -> {
//                result += if(beforeR == '\n') "하"
//                else "와"
//            }
//
//            'ゃ','ャ' -> {
//                result = result.replace(result.length, beforeR.assembleWithJung('야'))
//            }
//            'ゅ','ュ' -> {
//                result = result.replace(result.length, beforeR.assembleWithJung('유'))
//            }
//            'ょ','ョ' -> {
//                result = result.replace(result.length, beforeR.assembleWithJung('요'))
//            }
//
//            'ん','ン' -> {
//                result = result.replace(result.length, beforeR.assembleWithJong('안'))
//            }
//            'っ','ッ' -> {
//                result = result.replace(result.length, beforeR.assembleWithJong('앗'))
//            }
//            else -> result += (Hiragana.intonations[c]?: Katakana.intonations[c])?: c
//        }
//    }
//
//    return result
//}
//
//fun Char.assembleWithJung(jung: Char): Char {
//    return assembleHangul(cho = this.getChosung(), jung = jung.getJungsung())
//}
//fun Char.assembleWithJong(jong: Char): Char {
//    return assembleHangul(cho = this.getChosung(), jung = this.getJungsung(), jong = jong.getJongsung())
//}
//
//fun String.replace(i: Int, to: Char) = this.replace(i, to.toString())
//fun String.replace(i: Int, to: String): String {
//    return this.substring(0..<i-1)+to
//}
//
//object Hiragana {
//    val intonations = mapOf(
//        'あ' to '아', 'い' to '이', 'う' to '우', 'え' to '에', 'お' to '오',
//        'か' to '카', 'き' to '키', 'く' to '쿠', 'け' to '케', 'こ' to '코',
//        'さ' to '사', 'し' to '시', 'す' to '스', 'せ' to '세', 'そ' to '소',
//        'さ' to '사', 'し' to '시', 'す' to '스', 'せ' to '세', 'そ' to '소',
//        'た' to '타', 'ち' to '치', 'つ' to '츠', 'て' to '테', 'と' to '토',
//        'な' to '나', 'に' to '니', 'ぬ' to '누', 'ね' to '네', 'の' to '노',
//        'は' to '하', 'ひ' to '히', 'ふ' to '후', 'へ' to '헤', 'ほ' to '호',
//        'ま' to '마', 'み' to '미', 'む' to '무', 'め' to '메', 'も' to '모',
//        'や' to '야',             'ゆ' to '유',             'よ' to '요',
//        'ら' to '라', 'り' to '리', 'る' to '루', 'れ' to '레', 'ろ' to '로',
//        'わ' to '와', 'を' to '오',
//
//        'が' to '가', 'ぎ' to '기', 'ぐ' to '구', 'げ' to '게', 'ご' to '고',
//        'ざ' to '자', 'じ' to '지', 'ず' to '즈', 'ぜ' to '제', 'ぞ' to '조',
//        'だ' to '다', 'ぢ' to '지', 'づ' to '즈', 'で' to '데', 'ど' to '도',
//        'ば' to '바', 'び' to '비', 'ぶ' to '부', 'べ' to '베', 'ぼ' to '보',
//        'ぱ' to '파', 'ぴ' to '피', 'ぷ' to '푸', 'ぺ' to '페', 'ぽ' to '포',
//    )
//
//    object Columns {
//        val a = listOf('あ', 'か', 'さ', 'た', 'な', 'は', 'ま', 'や', 'ら', 'わ')
//        val i = listOf('い', 'き', 'し', 'ち', 'に', 'ひ', 'み', 'り')
//        val u = listOf('う', 'く', 'す', 'つ', 'ぬ', 'ふ', 'む', 'ゆ', 'る')
//        val e = listOf('え', 'け', 'せ', 'て', 'ね', 'へ', 'め', 'れ')
//        val o = listOf('お', 'こ', 'そ', 'と', 'の', 'ほ', 'も', 'よ', 'ろ')
//    }
//}
//
//object Katakana {
//    val intonations = mapOf(
//        'ア' to '아', 'イ' to '이', 'ウ' to '우', 'エ' to '에', 'オ' to '오',
//        'カ' to '카', 'キ' to '키', 'ク' to '쿠', 'ケ' to '케', 'コ' to '코',
//        'サ' to '사', 'シ' to '시', 'ス' to '스', 'セ' to '세', 'ソ' to '소',
//        'タ' to '타', 'チ' to '치', 'ツ' to '즈', 'テ' to '테', 'ト' to '토',
//        'ナ' to '나', 'ニ' to '니', 'ヌ' to '누', 'ネ' to '네', 'ノ' to '노',
//        'ハ' to '하', 'ヒ' to '히', 'フ' to '후', 'ヘ' to '헤', 'ホ' to '호',
//        'マ' to '마', 'ミ' to '미', 'ム' to '무', 'メ' to '메', 'モ' to '모',
//        'ヤ' to '야', 'ユ' to '유', 'ヨ' to '요',
//        'ラ' to '라', 'リ' to '리', 'ル' to '루', 'レ' to '레', 'ロ' to '로',
//        'ワ' to '와', 'ヲ' to '오',
//
//        'ガ' to '가', 'ギ' to '기', 'グ' to '구', 'ゲ' to '게', 'ゴ' to '고',
//        'ザ' to '자', 'ジ' to '지', 'ズ' to '주', 'ゼ' to '제', 'ゾ' to '조',
//        'ダ' to '다', 'ヂ' to '지', 'ヅ' to '즈', 'デ' to '데', 'ド' to '도',
//        'バ' to '바', 'ビ' to '비', 'ブ' to '부', 'ベ' to '베', 'ボ' to '보',
//        'パ' to '파', 'ピ' to '피', 'プ' to '푸', 'ペ' to '페', 'ポ' to '포',
//    )
//
//    object Columns {
//        val a = listOf(
//            'ア', 'カ', 'サ', 'タ', 'ナ', 'ハ', 'マ', 'ヤ', 'ラ',
//            'ガ', 'ザ', 'ダ', 'バ', 'パ'
//        )
//        val i = listOf(
//            'イ', 'キ', 'シ', 'チ', 'ニ', 'ヒ', 'ミ', 'リ',
//            'ギ', 'ジ', 'ヂ', 'ビ', 'ピ'
//        )
//        val u = listOf(
//            'ウ', 'ク', 'ス', 'ツ', 'ヌ', 'フ', 'ム', 'ユ', 'ル',
//            'グ', 'ズ', 'ヅ', 'ブ', 'プ'
//        )
//        val e = listOf(
//            'エ', 'ケ', 'セ', 'テ', 'ネ', 'ヘ', 'メ', 'レ',
//            'ゲ', 'ゼ', 'デ', 'ベ', 'ペ'
//        )
//        val o = listOf(
//            'オ', 'コ', 'ソ', 'ト', 'ノ', 'ホ', 'モ', 'ヨ', 'ロ',
//            'ゴ', 'ゾ', 'ド', 'ボ', 'ポ'
//        )
//    }
//}