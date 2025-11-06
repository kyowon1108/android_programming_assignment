"""
Date Utilities
날짜 관련 유틸리티 함수
"""

from datetime import date, timedelta
from typing import Tuple


def get_week_number(target_date: date) -> Tuple[int, int]:
    """
    Get ISO week number for a date

    Args:
        target_date: Date to get week number for

    Returns:
        Tuple of (year, week_number)
    """
    iso_calendar = target_date.isocalendar()
    return (iso_calendar[0], iso_calendar[1])


def get_week_dates(year: int, week_number: int) -> Tuple[date, date]:
    """
    Get start and end dates for a week

    Args:
        year: Year
        week_number: ISO week number

    Returns:
        Tuple of (start_date, end_date)
    """
    # Get first day of year
    jan_1 = date(year, 1, 1)

    # Find first Monday of year
    days_to_monday = (7 - jan_1.weekday()) % 7
    if days_to_monday == 0 and jan_1.weekday() != 0:
        days_to_monday = 7
    first_monday = jan_1 + timedelta(days=days_to_monday)

    # Calculate start date of target week
    start_date = first_monday + timedelta(weeks=week_number - 1)

    # End date is 6 days after start
    end_date = start_date + timedelta(days=6)

    return (start_date, end_date)


def calculate_streak(last_diary_date: date, new_diary_date: date, current_streak: int) -> int:
    """
    Calculate new streak value

    Args:
        last_diary_date: Date of last diary entry
        new_diary_date: Date of new diary entry
        current_streak: Current streak value

    Returns:
        Updated streak value
    """
    if not last_diary_date:
        return 1

    days_diff = (new_diary_date - last_diary_date).days

    if days_diff == 1:
        # Consecutive day
        return current_streak + 1
    elif days_diff == 0:
        # Same day (update existing)
        return current_streak
    else:
        # Streak broken
        return 1
