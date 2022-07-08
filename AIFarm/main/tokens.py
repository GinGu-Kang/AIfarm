from django.contrib.auth.tokens import PasswordResetTokenGenerator
from django.utils import six
import datetime
class AccountActivationTokenGenerator(PasswordResetTokenGenerator):
    def _make_hash_value(self, user,timestamp):
        return (six.text_type(user.pk)+
                six.text_type(user.username)+
                six.text_type(datetime.datetime.now()))


account_activation_token = AccountActivationTokenGenerator()