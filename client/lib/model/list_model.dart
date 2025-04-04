import 'package:flutter/cupertino.dart';

class ListModel<E> with ChangeNotifier implements Iterable<E> {
  final List<E> _list = [];

  @override
  int get length => _list.length;

  @override
  bool get isEmpty => _list.isEmpty;

  @override
  bool get isNotEmpty => _list.isNotEmpty;

  @override
  Iterator<E> get iterator => _list.iterator;

  Iterable<E> get reversed => _list.reversed;

  ListModel({List<E>? initialItems}) {
    if (initialItems != null) {
      _list.addAll(initialItems);
    }
  }

  add(E value) {
    _list.add(value);
    notifyListeners();
  }

  insert(int index, E value) {
    _list.insert(index, value);
    notifyListeners();
  }

  remove(E value) {
    _list.remove(value);
    notifyListeners();
  }

  @override
  bool any(bool Function(E element) test) => _list.any(test);

  @override
  Iterable<R> cast<R>() => _list.cast<R>();

  @override
  bool contains(Object? element) => _list.contains(element);

  @override
  E elementAt(int index) => _list.elementAt(index);

  @override
  bool every(bool Function(E element) test) => _list.every(test);

  @override
  Iterable<T> expand<T>(Iterable<T> Function(E element) toElements) =>
      _list.expand(toElements);

  @override
  E get first => _list.first;

  @override
  E firstWhere(bool Function(E element) test, {E Function()? orElse}) =>
      _list.firstWhere(test, orElse: orElse);

  @override
  T fold<T>(T initialValue, T Function(T previousValue, E element) combine) =>
      _list.fold(initialValue, combine);

  @override
  Iterable<E> followedBy(Iterable<E> other) => _list.followedBy(other);

  @override
  void forEach(void Function(E element) action) => _list.forEach(action);

  @override
  String join([String separator = ""]) => _list.join(separator);

  @override
  E get last => _list.last;

  @override
  E lastWhere(bool Function(E element) test, {E Function()? orElse}) =>
      _list.lastWhere(test, orElse: orElse);

  @override
  Iterable<T> map<T>(T Function(E e) toElement) => _list.map(toElement);

  @override
  E reduce(E Function(E value, E element) combine) => _list.reduce(combine);

  @override
  E get single => _list.single;

  @override
  E singleWhere(bool Function(E element) test, {E Function()? orElse}) =>
      _list.singleWhere(test, orElse: orElse);

  @override
  Iterable<E> skip(int count) => _list.skip(count);

  @override
  Iterable<E> skipWhile(bool Function(E value) test) => _list.skipWhile(test);

  @override
  Iterable<E> take(int count) => _list.take(count);

  @override
  Iterable<E> takeWhile(bool Function(E value) test) => _list.takeWhile(test);

  @override
  List<E> toList({bool growable = true}) => _list.toList(growable: growable);

  @override
  Set<E> toSet() => _list.toSet();

  @override
  Iterable<E> where(bool Function(E element) test) => _list.where(test);

  @override
  Iterable<T> whereType<T>() => _list.whereType();

  operator [](int index) => _list[index];

  operator []=(int index, E value) {
    _list[index] = value;
    notifyListeners();
  }
}
