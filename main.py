# -*- coding: utf-8 -*-
"""
SensiPro — Мушовири ҳассосияти Free Fire
Барнома device-и корбарро месанҷад ва ду профили ҳассосият
(Ҷанги пеш / Ҷанги дур) тавлид мекунад.

Ин барнома ҳеҷ гуна файли бозиро дигаргун намекунад, ҳеҷ хотираи
дигар барномаро дастрас намекунад ва ҳеҷ бартарии ноодилона намедиҳад —
он танҳо арзишҳои тавсиявии ҳассосиятро ҳисоб карда, ба корбар
нишон медиҳад, то худи корбар онҳоро дар танзимоти бозӣ дастӣ ворид кунад.
"""

import os
import platform
import sqlite3
import json

from kivy.app import App
from kivy.core.window import Window
from kivy.uix.boxlayout import BoxLayout
from kivy.uix.label import Label
from kivy.uix.button import Button
from kivy.uix.scrollview import ScrollView
from kivy.uix.popup import Popup
from kivy.graphics import Color, Rectangle
from kivy.metrics import dp
from kivy.utils import platform as kivy_platform

APP_NAME = "SensiPro"

# ---------- Ранг (Dark / Free Fire themed) ----------
COLOR_BG = (0.05, 0.05, 0.07, 1)
COLOR_CARD = (0.11, 0.11, 0.14, 1)
COLOR_ACCENT = (1.0, 0.42, 0.0, 1)      # афкории Free Fire
COLOR_ACCENT2 = (0.0, 0.85, 0.65, 1)    # сабзи неон
COLOR_TEXT = (0.95, 0.95, 0.95, 1)
COLOR_MUTED = (0.65, 0.65, 0.7, 1)


# =====================================================================
# 1. Санҷиши дастгоҳ
# =====================================================================
class DeviceInfo:
    """Ҷамъоварии маълумот дар бораи дастгоҳ бо истифода аз китобхонаҳои
    стандартии Python/Android. Ҳама амалиёт танҳо хониш (read-only) аст."""

    def __init__(self):
        self.device_name = "Номаълум"
        self.model = "Номаълум"
        self.ram_total_mb = 0
        self.ram_free_mb = 0
        self.storage_total_gb = 0.0
        self.android_version = platform.release()
        self._collect()

    def _collect(self):
        try:
            self.model = platform.uname().machine or "Номаълум"
        except Exception:
            pass

        if kivy_platform == "android":
            try:
                from jnius import autoclass
                Build = autoclass("android.os.Build")
                self.model = f"{Build.MANUFACTURER} {Build.MODEL}"
                self.device_name = Build.DEVICE
            except Exception:
                pass

            try:
                from jnius import autoclass
                Context = autoclass("android.content.Context")
                PythonActivity = autoclass("org.kivy.android.PythonActivity")
                activity = PythonActivity.mActivity
                am = activity.getSystemService(Context.ACTIVITY_SERVICE)
                MemoryInfo = autoclass("android.app.ActivityManager$MemoryInfo")
                mi = MemoryInfo()
                am.getMemoryInfo(mi)
                self.ram_total_mb = int(mi.totalMem / (1024 * 1024))
                self.ram_free_mb = int(mi.availMem / (1024 * 1024))
            except Exception:
                self._fallback_ram()
        else:
            self._fallback_ram()

        try:
            st = os.statvfs("/data" if os.path.exists("/data") else "/")
            self.storage_total_gb = round(
                (st.f_blocks * st.f_frsize) / (1024 ** 3), 1
            )
        except Exception:
            self.storage_total_gb = 0.0

        if self.device_name == "Номаълум":
            self.device_name = platform.node() or "Дастгоҳи корбар"

    def _fallback_ram(self):
        try:
            with open("/proc/meminfo") as f:
                meminfo = f.read()
            total = int([l for l in meminfo.splitlines() if "MemTotal" in l][0].split()[1])
            avail = int([l for l in meminfo.splitlines() if "MemAvailable" in l][0].split()[1])
            self.ram_total_mb = total // 1024
            self.ram_free_mb = avail // 1024
        except Exception:
            self.ram_total_mb = 4096
            self.ram_free_mb = 2048


# =====================================================================
# 2. AI Sensitivity Logic
# =====================================================================
class SensitivityAI:
    def __init__(self, device: DeviceInfo):
        self.device = device
        self.score = self._compute_score()
        self.close_range = self._build_close_range()
        self.long_range = self._build_long_range()
        self.dpi = self._recommend_dpi()
        self.general_sens = self._recommend_general()

    def _compute_score(self):
        score = 0
        ram = self.device.ram_total_mb

        if ram >= 8000:
            score += 45
        elif ram >= 6000:
            score += 38
        elif ram >= 4000:
            score += 28
        elif ram >= 3000:
            score += 18
        else:
            score += 10

        try:
            android_ver = int(str(self.device.android_version).split(".")[0])
        except Exception:
            android_ver = 10

        if android_ver >= 13:
            score += 25
        elif android_ver >= 11:
            score += 20
        elif android_ver >= 9:
            score += 14
        else:
            score += 8

        if self.device.storage_total_gb >= 128:
            score += 15
        elif self.device.storage_total_gb >= 64:
            score += 10
        else:
            score += 5

        free_ratio = 0
        if self.device.ram_total_mb:
            free_ratio = self.device.ram_free_mb / max(self.device.ram_total_mb, 1)
        score += int(free_ratio * 15)

        return max(0, min(100, score))

    def _build_close_range(self):
        base = 75 + int(self.score * 0.20)
        base = max(60, min(99, base))
        return {
            "General":      min(100, base + 5),
            "Red Dot":      base,
            "2x Scope":     max(1, base - 20),
            "4x Scope":     max(1, base - 35),
            "Sniper Scope": max(1, base - 55),
            "Free Look":    min(100, base + 8),
        }

    def _build_long_range(self):
        base = 45 + int(self.score * 0.15)
        base = max(30, min(65, base))
        return {
            "General":      base + 10,
            "Red Dot":      base,
            "2x Scope":     max(1, base - 10),
            "4x Scope":     max(1, base - 18),
            "Sniper Scope": max(1, base - 25),
            "Free Look":    base + 5,
        }

    def _recommend_dpi(self):
        if self.score >= 80:
            return 400
        elif self.score >= 60:
            return 380
        elif self.score >= 40:
            return 350
        else:
            return 320

    def _recommend_general(self):
        return min(100, 85 + int(self.score * 0.15))


# =====================================================================
# 3. Хазинаи маҳаллӣ (SQLite)
# =====================================================================
def save_profiles(ai: SensitivityAI, db_path):
    try:
        conn = sqlite3.connect(db_path)
        cur = conn.cursor()
        cur.execute("""
            CREATE TABLE IF NOT EXISTS profiles (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                profile_name TEXT,
                data TEXT,
                score INTEGER
            )
        """)
        cur.execute("DELETE FROM profiles")
        cur.execute(
            "INSERT INTO profiles (profile_name, data, score) VALUES (?, ?, ?)",
            ("close_range", json.dumps(ai.close_range), ai.score),
        )
        cur.execute(
            "INSERT INTO profiles (profile_name, data, score) VALUES (?, ?, ?)",
            ("long_range", json.dumps(ai.long_range), ai.score),
        )
        conn.commit()
        conn.close()
    except Exception:
        pass


# =====================================================================
# 4. GUI
# =====================================================================
class RootWidget(BoxLayout):
    def __init__(self, **kwargs):
        super().__init__(orientation="vertical", padding=dp(16), spacing=dp(12), **kwargs)

        with self.canvas.before:
            Color(*COLOR_BG)
            self._bg = Rectangle(pos=self.pos, size=self.size)
        self.bind(pos=self._update_bg, size=self._update_bg)

        self.device = DeviceInfo()
        self.ai = SensitivityAI(self.device)

        try:
            data_dir = App.get_running_app().user_data_dir
            os.makedirs(data_dir, exist_ok=True)
            save_profiles(self.ai, os.path.join(data_dir, "sensipro.db"))
        except Exception:
            pass

        self._build_header()
        self._build_device_card()
        self._build_score_card()
        self._build_buttons()
        self._build_dpi_card()
        self._build_footer()

    def _update_bg(self, *args):
        self._bg.pos = self.pos
        self._bg.size = self.size

    def _card(self, height=None):
        box = BoxLayout(
            orientation="vertical",
            padding=dp(12),
            spacing=dp(6),
            size_hint_y=None,
        )
        box.height = height or dp(120)
        with box.canvas.before:
            Color(*COLOR_CARD)
            rect = Rectangle(pos=box.pos, size=box.size)

        def _upd(inst, *a):
            rect.pos = inst.pos
            rect.size = inst.size

        box.bind(pos=_upd, size=_upd)
        return box

    def _build_header(self):
        header = Label(
            text="[b]SENSIPRO[/b]\n[size=13]Мушовири ҳассосияти Free Fire[/size]",
            markup=True,
            color=COLOR_ACCENT,
            size_hint_y=None,
            height=dp(70),
            font_size=dp(26),
            halign="center",
        )
        header.bind(size=lambda *a: setattr(header, "text_size", header.size))
        self.add_widget(header)

    def _build_device_card(self):
        d = self.device
        card = self._card(height=dp(150))
        card.add_widget(Label(
            text="[b]Маълумоти дастгоҳ[/b]", markup=True,
            color=COLOR_TEXT, size_hint_y=None, height=dp(24), halign="left"
        ))
        info_lines = [
            f"Номи дастгоҳ: {d.device_name}",
            f"Модел: {d.model}",
            f"RAM умумӣ: {d.ram_total_mb} MB",
            f"RAM озод: {d.ram_free_mb} MB",
            f"Хотираи дохилӣ: {d.storage_total_gb} GB",
        ]
        for line in info_lines:
            lbl = Label(
                text=line, color=COLOR_MUTED, size_hint_y=None,
                height=dp(20), halign="left", font_size=dp(13)
            )
            lbl.bind(size=lambda inst, *a: setattr(inst, "text_size", inst.size))
            card.add_widget(lbl)
        self.add_widget(card)

    def _build_score_card(self):
        card = self._card(height=dp(60))
        lbl = Label(
            text=f"Баҳои дастгоҳ (AI Score): [b]{self.ai.score}/100[/b]",
            markup=True, color=COLOR_ACCENT2, font_size=dp(15)
        )
        card.add_widget(lbl)
        self.add_widget(card)

    def _build_buttons(self):
        row = BoxLayout(size_hint_y=None, height=dp(56), spacing=dp(10))
        btn_close = Button(
            text="Танзимоти Ҷанги Пеш",
            background_normal="", background_color=COLOR_ACCENT,
            color=(1, 1, 1, 1), bold=True,
        )
        btn_long = Button(
            text="Танзимоти Ҷанги Дур",
            background_normal="", background_color=COLOR_ACCENT2,
            color=(0, 0, 0, 1), bold=True,
        )
        btn_close.bind(on_release=lambda *a: self._show_profile("Ҷанги пеш (Close Range)", self.ai.close_range))
        btn_long.bind(on_release=lambda *a: self._show_profile("Ҷанги дур (Long Range)", self.ai.long_range))
        row.add_widget(btn_close)
        row.add_widget(btn_long)
        self.add_widget(row)

    def _build_dpi_card(self):
        card = self._card(height=dp(90))
        card.add_widget(Label(
            text="[b]Тавсияи DPI ва Ҳассосияти умумӣ[/b]", markup=True,
            color=COLOR_TEXT, size_hint_y=None, height=dp(22), font_size=dp(13)
        ))
        card.add_widget(Label(
            text=f"DPI тавсияшуда: {self.ai.dpi}   |   General max: {self.ai.general_sens}%",
            color=COLOR_MUTED, size_hint_y=None, height=dp(22), font_size=dp(13)
        ))
        card.add_widget(Label(
            text="Роҳнамо: Танзимот > Дисплей > Хусусиятҳои иловагӣ > Ҳассосияти сенсор",
            color=COLOR_MUTED, size_hint_y=None, height=dp(22), font_size=dp(11)
        ))
        self.add_widget(card)

    def _build_footer(self):
        note = Label(
            text="Барои татбиқи танзимот, лутфан онҳоро дар худи бозӣ ворид кунед.",
            color=COLOR_ACCENT, size_hint_y=None, height=dp(50),
            font_size=dp(13), halign="center",
        )
        note.bind(size=lambda *a: setattr(note, "text_size", note.size))
        self.add_widget(note)

    def _show_profile(self, title, profile: dict):
        content = BoxLayout(orientation="vertical", padding=dp(12), spacing=dp(6))
        for key, val in profile.items():
            content.add_widget(Label(
                text=f"{key}: [b]{val}[/b]", markup=True, color=COLOR_TEXT,
                size_hint_y=None, height=dp(28)
            ))
        close_btn = Button(
            text="Хуб", size_hint_y=None, height=dp(44),
            background_normal="", background_color=COLOR_ACCENT,
        )
        content.add_widget(close_btn)

        popup = Popup(
            title=title,
            content=content,
            size_hint=(0.85, 0.7),
            background_color=COLOR_CARD,
            title_color=COLOR_ACCENT,
        )
        close_btn.bind(on_release=popup.dismiss)
        popup.open()


class ScrollableRoot(ScrollView):
    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.add_widget(RootWidget(size_hint_y=None, height=dp(720)))


class SensiProApp(App):
    def build(self):
        self.title = APP_NAME
        Window.clearcolor = COLOR_BG
        return ScrollableRoot()


if __name__ == "__main__":
    SensiProApp().run()
