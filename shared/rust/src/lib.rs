fn common() -> i32 {
    return 42;
}


#[cfg(test)]
mod tests {
    #[test]
    fn it_works() {
        assert_eq!(common(), 42);
    }
}
